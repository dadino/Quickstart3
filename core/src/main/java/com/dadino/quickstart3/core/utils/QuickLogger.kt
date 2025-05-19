package com.dadino.quickstart3.core.utils

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.Executors

object QuickLogger {
  private const val GO_ASYNC = true

  private var tag: String? = null
  private var isLoggingEnabled = false

  // a single-thread background dispatcher
  private val loggerDispatcher: CoroutineDispatcher by lazy {
	Executors.newSingleThreadExecutor { Thread(it, "LoggerThread") }
	  .asCoroutineDispatcher()
  }
  private val scope: CoroutineScope by lazy { CoroutineScope(SupervisorJob() + loggerDispatcher) }

  /**
   * Overrides the default logging behavior.
   *
   * This function allows you to explicitly enable or disable logging
   * for the current session.
   *
   * @param enable `true` to enable logging, `false` to disable logging.
   */
  fun enableLogging(enable: Boolean) {
	isLoggingEnabled = enable
  }

  /** Set a one-time tag for use on the next logging call. */
  fun tag(tag: String?): QuickLogger {
	this.tag = tag
	return this
  }

  /** Log a verbose message that will be evaluated lazily when the message is printed */
  fun v(message: () -> String?) {
	log(tag, Log.VERBOSE, message)
	tag = null
  }

  /** Log a debug message that will be evaluated lazily when the message is printed */
  fun d(message: () -> String?) {
	log(tag, Log.DEBUG, message)
	tag = null
  }

  /** Log an info message that will be evaluated lazily when the message is printed */
  fun i(message: () -> String?) {
	log(tag, Log.INFO, message)
	tag = null
  }

  /** Log a warning message that will be evaluated lazily when the message is printed */
  fun w(message: () -> String?) {
	log(tag, Log.WARN, message)
	tag = null
  }

  /** Log an error exception and a message that will be evaluated lazily when the message is printed */
  fun e(message: () -> String?) {
	log(tag, Log.ERROR, message)
	tag = null
  }

  // These functions just forward to the real timber. They aren't necessary, but they allow method
  // chaining like the normal Timber interface.

  /** A view into Timber's planted trees as a tree itself. */
  @JvmStatic
  fun asTree(): Timber.Tree = Timber.asTree()

  /** Add a new logging tree. */
  @JvmStatic
  fun plant(tree: Timber.Tree) = Timber.plant(tree)

  /** A view into Timber's planted trees as a tree itself. */
  @JvmStatic
  fun uproot(tree: Timber.Tree) = Timber.uproot(tree)

  /** Set a one-time tag for use on the next logging call. */
  @JvmStatic
  fun uprootAll() = Timber.uprootAll()

  /** A [Timber.Tree] for debug builds. Automatically infers the tag from the calling class. */
  @JvmStatic
  fun DebugTree() = Timber.DebugTree()

  private fun log(tag: String? = null, level: Int, messageBlock: () -> String?) {
	if (GO_ASYNC) logAsync(tag, level, messageBlock)
	else logSync(tag, level, messageBlock)
  }

  /**
   * Private function to log a message synchronously to Timber.
   *
   * This function checks if logging is enabled (either in DEBUG mode or via `logRepo`)
   * before actually logging the message. The actual Timber logging call is performed
   * based on the provided log level.
   *
   * @param tag Optional tag for the log message. If null, the default Timber tag will be used.
   * @param level The log level (e.g., `Log.VERBOSE`, `Log.DEBUG`).
   * @param messageBlock A lambda function that returns the message string to be logged.
   *                     This block is only evaluated if logging is enabled.
   */
  private fun logSync(tag: String? = null, level: Int, messageBlock: () -> String?) {
	// cheap sync guard
	if (Timber.treeCount == 0) return

	if (isLoggingEnabled) {
	  // run the actual Timber call on Default (Log.* is thread-safe)
	  val timber = if (tag != null) Timber.tag(tag) else Timber
	  when (level) {
		Log.VERBOSE -> timber.v(messageBlock())
		Log.DEBUG   -> timber.d(messageBlock())
		Log.INFO    -> timber.i(messageBlock())
		Log.WARN    -> timber.w(messageBlock())
		Log.ERROR   -> timber.e(messageBlock())
		Log.ASSERT  -> timber.wtf(messageBlock())
	  }
	}
  }

  /**
   * Always returns immediately with a Job.
   * The block runs off the main thread once the check passes.
   */
  private fun logAsync(tag: String? = null, level: Int, messageBlock: () -> String?): Job = scope.launch {
	logSync(tag, level, messageBlock)
  }
}