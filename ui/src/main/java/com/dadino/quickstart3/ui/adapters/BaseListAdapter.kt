package com.dadino.quickstart3.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.core.utils.QuickLogger
import com.dadino.quickstart3.core.utils.printQuickStackTrace
import com.dadino.quickstart3.ui.utils.OnDiffDispatchedCallbacks
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

abstract class BaseListAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : BaseAdapter<ITEM, HOLDER>() {

  protected var layoutInflater: LayoutInflater? = null

  var items: List<ITEM>? = null
	private set(items) {
	  field = items
	  count = NOT_COUNTED
	}

  private var diffDisposable: Disposable? = null
  fun setItemsAsyncWith(itemListCreationFunction: () -> List<ITEM>) {
	setItemsAsyncWith(null, itemListCreationFunction)
  }

  private var writeItemsToLogWhenDispatched: Boolean = false

  fun setWriteItemsToLogWhenDispatched(writeItemsToLogWhenDispatched: Boolean) {
	this.writeItemsToLogWhenDispatched = writeItemsToLogWhenDispatched
  }

  fun setItemsAsyncWith(onDiffDispatchedCallbacks: OnDiffDispatchedCallbacks?, itemListCreationFunction: () -> List<ITEM>) {
	diffDisposable?.dispose()
	var startTimeMillis = 0L
	diffDisposable = Single.fromCallable {
	  startTimeMillis = System.currentTimeMillis()
	  onDiffDispatchedCallbacks?.onDiffBegin()
	  val newItemList = touchItemListBeforeSend(itemListCreationFunction())
	  val oldItemList = items ?: listOf()
	  val callbacks = getDiffCallbacks(oldItemList, newItemList)
	  if (callbacks != null) ListWithDiff(newItemList, DiffUtil.calculateDiff(callbacks))
	  else throw RuntimeException("Trying to set items with Diff, but getDiffCallbacks is not set")
	}
	  .subscribeOn(Schedulers.computation())
	  .observeOn(AndroidSchedulers.mainThread())
	  .subscribeBy(
		onSuccess = {
		  items = it.list
		  items?.let { list ->
			if (writeItemsToLogWhenDispatched) {
			  val context = layoutInflater?.context
			  if (context != null) {
				QuickLogger.tag("ItemsDispatched").d { "Items dispatched: ${list.size}" }
				list.forEachIndexed { index, item ->
				  if (item is ContextFormattable) {
					QuickLogger.tag("ItemsDispatched").d { "$index -> ${item.format(context)?.toString()?.replace(Regex("\\R+"), " ")}" }
				  } else {
					QuickLogger.tag("ItemsDispatched").d { "$index -> ${item.toString().replace(Regex("\\R+"), " ")}" }
				  }
				}
			  }
			}
		  }
		  it.diffs.dispatchUpdatesTo(this)
		  onDiffDispatchedCallbacks?.onDiffDispatched(
			itemCount = items?.size ?: 0, executionTimeInMillis = System.currentTimeMillis() - startTimeMillis
		  )
		},
		onError = {
		  it.printQuickStackTrace("ItemsDispatched")
		  onDiffDispatchedCallbacks?.onDiffError(System.currentTimeMillis() - startTimeMillis)
		})
  }

  fun setItemsAsync(newItemList: List<ITEM>) {
	setItemsAsync(null, newItemList)
  }

  fun setItemsAsync(onDiffDispatchedCallbacks: OnDiffDispatchedCallbacks?, newItemList: List<ITEM>) {
	setItemsAsyncWith(onDiffDispatchedCallbacks) { newItemList }
  }

  fun setItemsSync(newItemList: List<ITEM>) {
	setItemsSync(null, newItemList)
  }

  fun setItemsSync(onDiffDispatchedCallbacks: OnDiffDispatchedCallbacks? = null, newItemList: List<ITEM>) {
	items = touchItemListBeforeSend(newItemList)
	notifyDataSetChanged()
	onDiffDispatchedCallbacks?.onDiffDispatched(itemCount = items?.size ?: 0, executionTimeInMillis = 0L)
  }

  private var count = NOT_COUNTED

  init {
	this.setHasStableIds(this.useStableId())
  }

  open fun getDiffCallbacks(oldList: List<ITEM>, newList: List<ITEM>): DiffUtil.Callback? {
	return null
  }

  open fun useStableId(): Boolean {
	return true
  }

  protected fun inflater(context: android.content.Context): LayoutInflater {
	if (layoutInflater == null) layoutInflater = LayoutInflater.from(context)
	return layoutInflater!!
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HOLDER {
	val holder = getHolder(parent, viewType)
	attachListenerToHolder(holder)
	return holder
  }

  override fun onBindViewHolder(holder: HOLDER, position: Int) {
	getItem(position)?.let { bindItem(holder, it, position) }
  }

  override fun getItemId(position: Int): Long {
	return if (getItem(position) != null) getItemIdSafe(position) else -1
  }

  private val mItemCount: Int
	get() {
	  if (count >= 0) return count
	  return if (this.items != null) {
		count = this.items!!.size + headersCount + footersCount
		count
	  } else {
		count = headersCount + footersCount
		count
	  }
	}

  override fun getItemCount(): Int {
	return mItemCount
  }

  protected abstract fun getItemIdSafe(position: Int): Long

  protected var footersCount: Int = 0

  protected var headersCount: Int = 0

  fun isLastItem(position: Int): Boolean {
	return position == mItemCount - 1
  }

  fun getPosition(id: Long): Int {
	if (mItemCount > 0) {
	  (0 until mItemCount)
		.filter { getItemId(it) == id }
		.forEach { return it }
	}
	return -1
  }

  fun getPosition(item: ITEM?, comparator: java.util.Comparator<ITEM>): Int {
	if (item == null) return -1
	if (mItemCount > 0) {
	  (0 until mItemCount)
		.filter { comparator.compare(getItem(it), item) == 0 }
		.forEach { return it }
	}
	return -1
  }

  open fun bindItem(holder: HOLDER, item: ITEM, position: Int) {
	holder.bindItem(item, position)
  }

  fun getItem(position: Int): ITEM? {
	if (position < headersCount) return null
	val adjustedPosition = position - headersCount
	return if (adjustedPosition < mItemCount - headersCount - footersCount)
	  this.items!![adjustedPosition]
	else
	  null
  }

  protected abstract fun getHolder(parent: ViewGroup, viewType: Int): HOLDER

  protected fun inflate(parent: ViewGroup, @androidx.annotation.LayoutRes layoutId: Int): android.view.View {
	return inflater(parent.context).inflate(layoutId, parent, false)
  }

  open fun touchItemListBeforeSend(itemList: List<ITEM>): List<ITEM> {
	return itemList
  }

  companion object {

	private const val NOT_COUNTED = -1
  }
}

data class ListWithDiff<E>(val list: List<E>, val diffs: DiffUtil.DiffResult)