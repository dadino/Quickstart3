package com.dadino.quickstart3.core.components

import android.view.View
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.interfaces.SectionParent
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy


abstract class Section<STATE : Any, VM : BaseViewModel<STATE>, out VIEW : View>(val parent: SectionParent) : Actionable {
	val view: VIEW = this.createView(parent)
	var viewModel: VM = this.createViewModel(parent)
		private set
	override val userActionsHandler = object : UserActionsHandler() {
		override fun collectUserActions(): Observable<UserAction> {
			return this@Section.collectUserActions()
		}

		override fun interceptUserAction(action: UserAction): UserAction {
			return this@Section.interceptUserAction(action)
		}
	}.apply { connect() }

	init {
		parent.attachDisposableToResumePause { viewModel.states.subscribeBy(onNext = { render(it) }) }
		parent.attachDisposableToResumePause { this.userActions().subscribe(viewModel.userActionsConsumer()) }
		parent.attachDisposableToResumePause { this.userActions().subscribe(parent.userActionsConsumer()) }
	}

	abstract fun createView(sectionParent: SectionParent): VIEW
	abstract fun createViewModel(sectionParent: SectionParent): VM
	abstract fun render(state: STATE)
	fun showError(error: Throwable) {
		parent.showError(error)
	}

	fun passUserActionToViewModel(action: UserAction) {
		viewModel.receiveUserAction(action)
	}

	companion object {
		fun attachToParent(sectionParent: SectionParent, section: Section<*, *, *>) {
			sectionParent.getSectionContainer().removeAllViews()
			sectionParent.getSectionContainer().addView(section.view)
		}
	}
}