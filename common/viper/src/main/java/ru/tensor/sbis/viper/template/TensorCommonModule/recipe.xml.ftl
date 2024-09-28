<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>
    <@kt.addAllKotlinDependencies />

	<instantiate from="src/app_package/contract/ZStubCommonDependency.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/${moduleName}CommonDependency.kt" />
	<instantiate from="src/app_package/contract/ZStubCommonFeature.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/${moduleName}CommonFeature.kt" />
	
	<instantiate from="src/app_package/di/ZStubCommonScope.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${moduleName}CommonScope.kt" />
	<instantiate from="src/app_package/di/ZStubCommonModule.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${moduleName}CommonModule.kt" />
	<instantiate from="src/app_package/di/ZStubCommonComponentProvider.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${moduleName}CommonComponentProvider.kt" />
	<instantiate from="src/app_package/di/ZStubCommonComponentInitializer.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${moduleName}CommonComponentInitializer.kt" />
	<instantiate from="src/app_package/di/ZStubCommonComponentHolder.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${moduleName}CommonComponentHolder.kt" />
	<instantiate from="src/app_package/di/ZStubCommonComponent.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${moduleName}CommonComponent.kt" />

	<instantiate from="src/app_package/crud/.gitkeep" to="${escapeXmlAttribute(srcOut)}/crud/.gitkeep" />
	<instantiate from="src/app_package/feature/.gitkeep" to="${escapeXmlAttribute(srcOut)}/feature/.gitkeep" />
	<instantiate from="src/app_package/model/.gitkeep" to="${escapeXmlAttribute(srcOut)}/model/.gitkeep" />
	<instantiate from="src/app_package/ui/.gitkeep" to="${escapeXmlAttribute(srcOut)}/ui/.gitkeep" />

    <instantiate from="res/values/attr.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/attr.xml" />
    <instantiate from="res/values/colors.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/colors.xml" />
    <instantiate from="res/values/dimens.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/dimens.xml" />
    <instantiate from="res/values/ids.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/ids.xml" />
    <instantiate from="res/values/strings.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/strings.xml" />
    <instantiate from="res/values/styles.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/styles.xml" />
	
	<instantiate from="res/AndroidManifest.xml.ftl" to="${escapeXmlAttribute(resOut)}/AndroidManifest.xml" />

</recipe>
