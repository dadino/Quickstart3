package ${packageName}.${featurePackage}

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal


sealed class ${featureName}Effect : SideEffect() {

}

sealed class ${featureName}Signal : Signal() {

}

sealed class ${featureName}Event : Event() {

}