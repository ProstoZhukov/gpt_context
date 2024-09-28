<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>
    <@kt.addAllKotlinDependencies />
		
	<!-- gen folder -->
	<#if includeGen>
		<instantiate from="src/app_package/common/crud/gen/ListResultOfModel.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/gen/ListResultOf${modelName}.kt" />
		<instantiate from="src/app_package/common/crud/gen/Model.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/gen/${modelName}.kt" />
		<instantiate from="src/app_package/common/crud/gen/ModelFacade.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/gen/${modelName}Manager.kt" />
		<instantiate from="src/app_package/common/crud/gen/ModelFilter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/gen/${modelName}Filter.kt" />
    </#if>
	
	<#if includeCrud || includeList>
		<!-- mocks -->
		<instantiate from="src/app_package/common/crud/mocks/Beans.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/mocks/Beans.kt" />
		
		<instantiate from="src/app_package/common/model/Model.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/model/${modelName}.kt" />
			   
		<!-- crud classes -->
		<instantiate from="src/app_package/common/crud/BlankCommandWrapper.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/${modelName}CommandWrapper.kt" />		   
		<instantiate from="src/app_package/common/crud/BlankCommandWrapperImpl.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/${modelName}CommandWrapperImpl.kt" />		   
		<instantiate from="src/app_package/common/crud/BlankRepository.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/${modelName}Repository.kt" />		   
		<instantiate from="src/app_package/common/crud/BlankRepositoryImpl.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/${modelName}RepositoryImpl.kt" />		   
		<instantiate from="src/app_package/common/crud/BlankRepositoryMockImpl.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/${modelName}RepositoryMockImpl.kt" />
		
		<#if includeList>
			<instantiate from="src/app_package/common/crud/BlankListFilter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/${modelName}ListFilter.kt" />
			<instantiate from="src/app_package/common/crud/mapper/ModelListMapper.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/mapper/${modelName}ListMapper.kt" />
		</#if>
		
		<!-- model mapper -->
		<#if includeCrud>
			<instantiate from="src/app_package/common/crud/mapper/ModelMapper.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/crud/${moduleName}/mapper/${modelName}Mapper.kt" />
		</#if>
		
	</#if>
	
	<!-- di classes -->
	<instantiate from="src/app_package/common/di/BlankModule.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/di/${modelName}Module.kt" />		   
	<instantiate from="src/app_package/common/di/BlankComponent.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/di/${modelName}Component.kt" />
	
	<!-- feature classes -->
	<instantiate from="src/app_package/common/feature/BlankFragmentProvider.kt.ftl" to="${escapeXmlAttribute(srcOut)}/common/feature/${moduleName}/ui/${modelName}FragmentProvider.kt" />

	<!-- module classes -->
	<instantiate from="src/app_package/module/viewmodel/ZStubViewModel.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/viewmodel/${modelName}ViewModel.kt" />
	
	<instantiate from="src/app_package/module/router/phone/ZStubPhoneRouter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/router/phone/${modelName}PhoneRouter.kt" />
	<instantiate from="src/app_package/module/router/tablet/ZStubTabletRouter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/router/tablet/${modelName}TabletRouter.kt" />
	
	<instantiate from="src/app_package/module/domain/interactor/ZStubInteractorImpl.kt.ftl" to="${escapeXmlAttribute(srcOut)}/domain/interactor/${modelName}InteractorImpl.kt" />
	
	<instantiate from="src/app_package/module/contract/ZStubDependency.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/${modelName}Dependency.kt" />
	<instantiate from="src/app_package/module/contract/ZStubFeature.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/${modelName}Feature.kt" />
	<instantiate from="src/app_package/module/contract/internal/ZStubInteractor.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/internal/${modelName}Interactor.kt" />
	<instantiate from="src/app_package/module/contract/internal/ZStubRouter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/internal/${modelName}Router.kt" />
	<instantiate from="src/app_package/module/contract/internal/ZStubViewContract.kt.ftl" to="${escapeXmlAttribute(srcOut)}/contract/internal/${modelName}ViewContract.kt" />
	
	<instantiate from="src/app_package/module/di/ZStubScope.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${modelName}Scope.kt" />
	<instantiate from="src/app_package/module/di/ZStubModule.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${modelName}Module.kt" />
	<instantiate from="src/app_package/module/di/ZStubComponentProvider.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${modelName}ComponentProvider.kt" />
	<instantiate from="src/app_package/module/di/ZStubComponentInitializer.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${modelName}ComponentInitializer.kt" />
	<instantiate from="src/app_package/module/di/ZStubComponentHolder.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${modelName}ComponentHolder.kt" />
	<instantiate from="src/app_package/module/di/ZStubComponent.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/${modelName}Component.kt" />
	<instantiate from="src/app_package/module/di/view/ZStubViewComponent.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/view/${modelName}ViewComponent.kt" />
	<instantiate from="src/app_package/module/di/view/ZStubViewModule.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/view/${modelName}ViewModule.kt" />
	<instantiate from="src/app_package/module/di/view/ZStubViewScope.kt.ftl" to="${escapeXmlAttribute(srcOut)}/di/view/${modelName}ViewScope.kt" />

	<#if includeList>
		<instantiate from="src/app_package/module/for_list/ZStubFragment.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/view/${modelName}Fragment.kt" />
		<instantiate from="src/app_package/module/for_list/ZStubPresenter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/presenter/${modelName}Presenter.kt" />
		<instantiate from="src/app_package/module/for_list/ZStubAdapter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/adapter/${modelName}Adapter.kt" />
		<instantiate from="src/app_package/module/for_list/ZStubHolder.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/adapter/holder/${modelName}Holder.kt" />
		<instantiate from="res/layout/blank_item.xml.ftl" to="${escapeXmlAttribute(resOut)}/layout/${moduleName}_item.xml" />
	<#else>
		<instantiate from="src/app_package/module/for_crud/ZStubFragment.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/view/${modelName}Fragment.kt" />
		<instantiate from="src/app_package/module/for_crud/ZStubPresenter.kt.ftl" to="${escapeXmlAttribute(srcOut)}/presentation/presenter/${modelName}Presenter.kt" />
	</#if>
	
    <instantiate from="res/values/attr.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/attr.xml" />
    <instantiate from="res/values/colors.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/colors.xml" />
    <instantiate from="res/values/dimens.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/dimens.xml" />
    <instantiate from="res/values/ids.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/ids.xml" />
    <instantiate from="res/values/strings.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/strings.xml" />
    <instantiate from="res/values/styles.xml.ftl" to="${escapeXmlAttribute(resOut)}/values/styles.xml" />
	
	<instantiate from="res/layout/fragment_blank.xml.ftl" to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(fragmentName)}.xml" />
	<instantiate from="res/AndroidManifest.xml.ftl" to="${escapeXmlAttribute(resOut)}/AndroidManifest.xml" />

</recipe>
