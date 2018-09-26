package ${packageName}.${featurePackage}

import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.SideEffectHandler
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.Next.Companion.justEffect
import com.dadino.quickstart3.core.entities.Next.Companion.justSignal
import com.dadino.quickstart3.core.entities.Next.Companion.justState
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.Next.Companion.stateAndSignal
import com.dadino.quickstart3.core.entities.Start.Companion.start
import com.dadino.quickstart3.core.entities.Start


class ${featureName}ViewModel : BaseViewModel<${featureName}State>() {
	init {
		connect()
	}

	override fun updateFunction() = { previous: ${featureName}State, event: Event ->
		when (event) {
			
			else                                      -> noChanges<${featureName}State>()
		}
	}

	override fun getStart() = start(${featureName}State())

	override fun getSideEffectHandlers() = 
	listOf<SideEffectHandler>(
	
	)
}

data class ${featureName}State(
    val deleteMe:String = ""
) : State()