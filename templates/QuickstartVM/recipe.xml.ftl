<?xml version="1.0"?>
<recipe>
 
    <instantiate from="src/app_package/ViewModel.kt.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${featurePackage}/${featureName}ViewModel.kt" />
  
    <instantiate from="src/app_package/Entities.kt.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${featurePackage}/${featureName}Entities.kt" />
   
    <instantiate from="src/app_package/EffectHandlers.kt.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${featurePackage}/${featureName}EffectHandlers.kt" />
 
    <open file="${srcOut}/${featureName}ViewModel.kt"/>
</recipe>