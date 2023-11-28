package com.pelayo.integrations.sisnetv6.helpers

//import com.pelayo.integrations.sisnetv6.utils.SisnetV6Constants
import com.pelayo.integrations.common.utils.CommonConstants
import com.pelayo.integrations.common.utils.CommonFunctions

/*
 * Inicialización requerida para la ejecución del BUILD de SISNetV6
 */
def sisnetV6Init(options){
    buildMavenProject(options)
}

// Construye un proyecto SISnetV6
def buildMavenProject(options) {
    def mvnHome = tool 'maven 3.6.0'

    def params = getParams(options)
    def mavenOptions = params.mavenOptions?: ''
    def mavenGoals = params.mavenGoals?: "clean install -U -DargLine='-Xms3g -Xmx3g'"
    configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
        //println("## CMD: sh(\"${mvnHome} $mavenGoals $mavenOptions -gs $MAVEN_SETTINGS_XML\")")
        //sh("mvn ${mavenGoals} ${mavenOptions} -gs ${MAVEN_SETTINGS_XML}")
        sh("${mvnHome} ${mavenGoals} ${mavenOptions} -gs ${MAVEN_SETTINGS_XML}")
    }
}

//Obtiene los parametros del objeto options
def getParams(options) {
    def params = [:]
    if (options != null) {
        options.resolveStrategy = Closure.DELEGATE_FIRST
        options.delegate = params
        options()
    }
    return params;
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   
    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"build");
}