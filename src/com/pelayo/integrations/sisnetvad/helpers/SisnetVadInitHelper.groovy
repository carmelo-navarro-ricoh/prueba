package com.pelayo.integrations.sisnetvad.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.common.utils.CommonConstants
import com.pelayo.integrations.sisnet.utils.SisnetConstants
import com.pelayo.integrations.sisnetvad.utils.SisnetVadConstants

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetVadInit(){
    def commonFunctions=new CommonFunctions()
    sisnetVadEnvironmentStart()
    if(env.publishWar=="true"){
        //Descarga del repositorio de sisnetVad (codigo fuente)
        commonFunctions.checkoutGit(env.gitURLSisnetVad, env.gitBranchSisnetVad, "./${env.workspaceSisnetVadRelativeDirSrc}")
    }
    //Descarga del repositorio de sisnetVad (Configuración)
    commonFunctions.checkoutGit(env.gitURLSisnetVadConfig, env.gitBranchSisnetVadConfig, "./${env.workspaceSisnetVadRelativeDirConfig}")
}

/*
 * Inicialización de valores del entorno para SisnetVad
 */
def sisnetVadEnvironmentStart(){
    // Variables necesarias para definir el repositorio GIT de SisnetVAD
    env.gitURLSisnetVad=SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".URL
    env.gitBranchSisnetVad=SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".GIT_BRANCH
    env.workspaceSisnetVadRelativeDirSrc=SisnetVadConstants.sisnetVadSourceDir

    // Variables necesarias para definir el repositorio GIT de SisnetVAD (Config)
    env.gitURLSisnetVadConfig=SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.URL
    env.gitBranchSisnetVadConfig=SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.BRANCH
//    env.gitURLSisnetVadConfig=SisnetVadConstants.ENTORNOS_SISNETVADCONFIG.MASTER.URL
//    env.gitBranchSisnetVadConfig=SisnetVadConstants.ENTORNOS_SISNETVADCONFIG.MASTER.GIT_BRANCH
    env.workspaceSisnetVadRelativeDirConfig=SisnetVadConstants.sisnetVadConfigDir
    env.gitSisnetVadConfigMavenPathPom=SisnetVadConstants.ENTORNOS_SISNETVADCONFIG.MASTER.PATH_POM."${env.TARGET_ENVIRONMENT}"

    //Ubicación de los artefactos generados por SisnetVad
    env.workspaceSisnetVadRelativeDirArtifact=SisnetVadConstants.sisnetVadArtifact
    }