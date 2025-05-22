package com.dadino.quickstart3.core.utils

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import timber.log.Timber

object QuickLogger {
  private var evaluateAsync = true

  private var tag: String? = null
  private var isLoggingEnabled = false

  // a single-thread background dispatcher
  @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
  private val loggerDispatcher: CoroutineDispatcher by lazy {
	newSingleThreadContext("LoggerContextThread")
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

  /**
   * Sets whether log messages should be evaluated asynchronously.
   *
   * If `async` is true, log messages will be evaluated in the `loggerDispatcher`,
   * a dedicated single-thread background dispatcher. Otherwise, they will be evaluated
   * synchronously on the calling thread.
   *
   * @param async True to evaluate log messages asynchronously using the `loggerDispatcher`,
   *              false to evaluate them synchronously.
   */
  fun setLogMessageEvaluationAsync(async: Boolean) {
	evaluateAsync = async
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

  fun printStackTrace(throwable: Throwable) {
	log(tag, Log.ERROR, null, throwable)
	tag = null
  }

  // These functions just forward to the real timber. They aren't necessary, but they allow method
  // chaining like the normal Timber interface.

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

  private fun log(tag: String? = null, level: Int, messageBlock: (() -> String?)?, throwable: Throwable? = null) {
	if (isLoggingEnabled) {
	  if (evaluateAsync) logAsync(tag, level, messageBlock, throwable)
	  else logSync(tag, level, messageBlock, throwable)
	}
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
  private fun logSync(tag: String? = null, level: Int, messageBlock: (() -> String?)?, throwable: Throwable? = null) {
	// run the actual Timber call on Default (Log.* is thread-safe)
	val timber = if (tag != null) Timber.tag(tag) else Timber
	when (level) {
	  Log.VERBOSE -> timber.v(throwable, messageBlock?.invoke())
	  Log.DEBUG   -> timber.d(throwable, messageBlock?.invoke())
	  Log.INFO    -> timber.i(throwable, messageBlock?.invoke())
	  Log.WARN    -> timber.w(throwable, messageBlock?.invoke())
	  Log.ERROR   -> timber.e(throwable, messageBlock?.invoke())
	  Log.ASSERT  -> timber.wtf(throwable, messageBlock?.invoke())
	}
  }

  /**
   * Always returns immediately with a Job.
   * The block runs off the main thread once the check passes.
   */
  private fun logAsync(tag: String? = null, level: Int, messageBlock: (() -> String?)?, throwable: Throwable? = null): Job = scope.launch {
	logSync(tag, level, messageBlock, throwable)
  }
}

fun Throwable.printQuickStackTrace(tag: String? = null) = (if (tag != null) QuickLogger.tag(tag) else QuickLogger).printStackTrace(this)