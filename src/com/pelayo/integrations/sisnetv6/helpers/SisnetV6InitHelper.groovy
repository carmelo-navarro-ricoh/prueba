package com.pelayo.integrations.sisnetv6.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.sisnetv6.utils.SisnetV6Constants
import com.pelayo.integrations.sisnetv6.utils.SisnetV6Functions

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetV6Init(){
    //Definimos que configuración de maven se va a utilizar
    env.ID_CUSTOM_MAVEN_SETTINGS = 'nexus-sisnet'
    //Inicializa y valida los despliegues del proyecto en Tomcat
    sisnetV6EnvironmentStart();
    //Definimos la versión del artefacto que se va a generar
    new CommonFunctions().initArtifactVersion()
    //Mostrar la parametrización de la ejecución
    sisnetV6PrintStartInfo()
}

/**
 * Valida que es posible desplegar en SISnetV6 con la configuracion especificada
 * @param props
 * @return
 */
def sisnetV6EnvironmentStart() {
    //Generación de variables de entorno a partir de la información de GIT
    initEnvironmentsFromBranch()	
    //Generación de variables de entorno a partir del POM
    initEnvironmentsFromPom()	
    //Generación de variables propias de SisnetV6
    initSisnetV6Version()
    // Descargamos los ficheros de configuración
    checkOutSISnetV6ConfigFiles(env.SISNET_V6_CONFIG_FILES_DIRECTORY, env.TARGET_ENVIRONMENTS, env.TARGET_ENVIRONMENT_CONFIG)
    movePoms(env.SISNET_V6_CONFIG_FILES_DIRECTORY, SisnetV6Constants.environments."${env.TARGET_ENVIRONMENTS}".CONFIG)
    //Actualización de los POMs de SisnetV6
    updatePoms(env.ARTIFACT_VERSION)
    updatePomsParent(env.ARTIFACT_VERSION)
    updatePomPropertiesSisnetFrameworkVersion(env.ARTIFACT_VERSION)
}

def movePoms(path,environment){
    //Esta configuración contiene el WebSISnet/pom.xml que originalmente no está en el repo de configuración
    def pomList=["pom.xml","WebSISnet/pom.xml","SISnetFramework/pom.xml","SISnetBusinessIO/pom.xml","SISnet/pom.xml","Pelayo/pom.xml"]
    //def pomList=["pom.xml","SISnetFramework/pom.xml","SISnetBusinessIO/pom.xml","SISnet/pom.xml","Pelayo/pom.xml"]
    pomList.each{ pom ->
        println("Moviendo el archivo de pom ./${path}/maven/${environment}/${pom}")
        sh("cp ./${path}/maven/${environment}/${pom} ./${pom}")
    }

}

//Actualización de la parentversion
def updatePomsParent(appVersion){
    def pomList=["./WebSISnet/pom.xml","./SISnetFramework/pom.xml","./SISnetBusinessIO/pom.xml","./SISnet/pom.xml","./Pelayo/pom.xml"]
    pomList.each{ pom ->
        def pomfile = readMavenPom file: "${pom}"
        pomfile.parent.version=appVersion
        writeMavenPom model: pomfile, file: "${pom}"
    }
}

//Actualización de la referencia al framework en los POM
def updatePomPropertiesSisnetFrameworkVersion(appVersion){
    def pomList=["./WebSISnet/pom.xml", "./SISnetBusinessIO/pom.xml", "./SISnet/pom.xml","./Pelayo/pom.xml"]
    pomList.each{ pom ->
        def pomFile = readMavenPom file: "${pom}"
        pomFile.properties."SISnetFramework.version"=appVersion
        writeMavenPom model: pomFile, file: "${pom}"
    }
}

//Actualización de la versión de los archivos POM
def updatePoms(appVersion){
    println("Versión: ${appVersion}")
    def pomList=["./pom.xml","./WebSISnet/pom.xml","./SISnetFramework/pom.xml","./SISnetBusinessIO/pom.xml","./SISnet/pom.xml","./Pelayo/pom.xml"]
    pomList.each{ pom ->
        def pomFile = readMavenPom(file: "${pom}")
        pomFile.version=appVersion
        writeMavenPom(file:"${pom}", model: pomFile)
    }
}

//Asignación de la versión del artefacto
def initSisnetV6Version(){
    switch(env.TARGET_ENVIRONMENT.toUpperCase()) {
        case ["INTG","ACPT","PREP"]:
            env.ARTIFACT_VERSION=new CommonFunctions().generateArtifactVersion(env.TARGET_ENVIRONMENT)+"-SNAPSHOT"
        break
        case ["PROD"]:
            env.ARTIFACT_VERSION=new CommonFunctions().generateArtifactVersionFullDate()
        break
    }
}

/**
 * Establecimiento del entorno de despliegue en base a la rama.
 */
def initEnvironmentsFromBranch() {
    // Si la rama es env/01-INTG se realiza el deploy en INTG
    // Si la rama es env/02-ACPT se realiza el deploy en ACPT
    // Si la rama es env/03-PRE se realiza el deploy en PREP
    // Si la rama es master se realiza el deploy en PROD
    // En otro caso no se configura el despliegue
    switch(env.BRANCH_NAME) {
        case SisnetV6Constants.branchesName.INTG:
            env.TARGET_ENVIRONMENTS = SisnetV6Constants.environments.INTG.TARGET
            env.TARGET_ENVIRONMENT = SisnetV6Constants.environments.INTG.TARGET
            env.TARGET_ENVIRONMENT_CONFIG = SisnetV6Constants.environments.INTG.CONFIG
            break
        case SisnetV6Constants.branchesName.ACPT:
            env.TARGET_ENVIRONMENTS = SisnetV6Constants.environments.ACPT.TARGET
            env.TARGET_ENVIRONMENT = SisnetV6Constants.environments.ACPT.TARGET
            env.TARGET_ENVIRONMENT_CONFIG = SisnetV6Constants.environments.ACPT.CONFIG
            break
        case SisnetV6Constants.branchesName.PREP:
            env.TARGET_ENVIRONMENTS = SisnetV6Constants.environments.PREP.TARGET
            env.TARGET_ENVIRONMENT = SisnetV6Constants.environments.PREP.TARGET
            env.TARGET_ENVIRONMENT_CONFIG = SisnetV6Constants.environments.PREP.CONFIG
            break
        case SisnetV6Constants.branchesName.PROD:
            env.TARGET_ENVIRONMENTS = SisnetV6Constants.environments.PROD.TARGET
            env.TARGET_ENVIRONMENT = SisnetV6Constants.environments.PROD.TARGET
            env.TARGET_ENVIRONMENT_CONFIG = SisnetV6Constants.environments.PROD.CONFIG
            break
        case ["feature/DevOpsTest"]: //Añadida para simular que es un despliegue en otro entorno
            env.TARGET_ENVIRONMENTS = SisnetV6Constants.environments.INTG.TARGET
            env.TARGET_ENVIRONMENT = SisnetV6Constants.environments.INTG.TARGET
            env.TARGET_ENVIRONMENT_CONFIG = SisnetV6Constants.environments.INTG.CONFIG
            break
        default:
            env.TARGET_ENVIRONMENTS = ""
            env.TARGET_ENVIRONMENT = ""
            env.TARGET_ENVIRONMENT_CONFIG = ""
        break
    }
}

def initEnvironmentsFromPom(){
    def pomFile = readMavenPom file: 'pom.xml'
    if (!pomFile.getArtifactId()?.trim()) {
        error "## ERROR: No se encuentra <artifactId> en pom.xml"
    }
    if (!pomFile.getVersion()?.trim()) {
        error "## ERROR: No se encuentra <version> en pom.xml"
    }
    if (!pomFile.getName()?.trim()) {
        error "## ERROR: No se encuentra <name> en pom.xml"
    }
    env.ARTIFACT_ID = pomFile.getArtifactId();
    env.ARTIFACT_GROUPID = pomFile.getGroupId();
    env.ARTIFACT_NAME = pomFile.getName();
}

def checkOutSISnetV6ConfigFiles(configDir, environment, environment_config) {
    def gitUrlAroConfig=""
    switch(environment.toUpperCase()) {
        case ["INTG","ACPT"]:
            gitUrlAroConfig = env.GIT_URL.replace('.git', '-config.git')           
        break
        case ["PREP","PROD"]:
            gitUrlAroConfig = env.GIT_URL.replace('.git', '-config-pro.git')
        break
    }
    sh "mkdir -p '${configDir}'"
    dir(configDir) {
        try {
            //git credentialsId: 'soprasteria-bitbucket', url: gitUrlAroConfig
            git url: gitUrlAroConfig
        } catch(Exception e) {
            println("## WARNING: No existe Repositorio de Configuración SISnetV6, usamos el valor por defecto pricing-rate-sisnetv6-config.git");
            git url: SisnetV6Constants.GIT_SISNET_LOCAL_CONFIG_URL
        }
    }
}

/*
 * Mostrar la información de que se va a ejecutar
 */
def sisnetV6PrintStartInfo(){
    println("""##############################################
## Información de la ejecución del pipeline ##
##############################################
## Aplicación: SISNETV6
## - Aplicación
##   · Entorno: ${env.TARGET_ENVIRONMENTS}
##   · Entorno config: ${env.TARGET_ENVIRONMENT_CONFIG}
## - Git: 
##   · Repositorio: ${env.GIT_URL}
##   · Branch: ${env.BRANCH_NAME}
##   · Commit: ${env.GIT_COMMIT}
## - Git (config): 
##   · Repositorio: ${env.GIT_URL.replace('.git', '-config.git')}
##   · Branch: master
## - Artefacto
##   · Nombre: ${env.ARTIFACT_NAME}
##   · ID: ${env.ARTIFACT_ID}
##   · Version: ${env.ARTIFACT_VERSION}
##   · Group ID: ${env.ARTIFACT_GROUPID}
##############################################""")
}
