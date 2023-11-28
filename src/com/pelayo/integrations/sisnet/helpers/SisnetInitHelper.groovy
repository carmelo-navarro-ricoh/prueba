package com.pelayo.integrations.sisnet.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.sisnet.utils.SisnetConstants
import com.pelayo.integrations.sisnetvad.utils.SisnetVadConstants
import com.pelayo.integrations.common.utils.CommonConstants
import groovy.json.JsonSlurperClassic

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetInit(props){
    env.ID_CUSTOM_MAVEN_SETTINGS = 'nexus-sisnet'
    def commonFunctions=new CommonFunctions()
    sisnetEnvironmentStart(props)
    sisnetPrintStartInfo()
    //commonFunctions.notifyByEmail("SISNET_START_PROCESS","","")
    if(env.publishWar=="true"){
        // Descarga del repositorio GIT de Sisnet
        commonFunctions.checkoutGit(env.gitURLSisnet, env.gitBranchSisnet, "./${env.workspaceSisnetRelativeDirSrc}")
    }
    // Descarga del repositorio GIT de configuracion de Sisnet
	commonFunctions.checkoutGit(SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.URL, SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.BRANCH, SisnetConstants.sisnetRelativeDirConfig)
}

/*
 * Inicialización de valores del entorno para sisnet
 */
def sisnetEnvironmentStart(props){
    def commonFunctions=new CommonFunctions()
    println("## INFO TEST: ${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".GIT_BRANCH}")
    // Descarga del repositorio git de código de sisnet
    switch(env.TARGET_ENVIRONMENT) {
        case ["INTG","ACPT","PREP","PROD"]:
            env.gitBranchSisnet=SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".GIT_BRANCH

            break
        default:
            error("## ERROR: No se ha introducido un entorno válido para el despliegue")
        break
    }
    env.gitURLSisnet=SisnetConstants.sisnetGitUrl
    env.workspaceSisnetRelativeDirSrc=SisnetConstants.sisnetRelativeDirSrc
    env.workspaceSisnetRelativeDirConfig =SisnetConstants.sisnetRelativeDirConfig
    env.workspaceSisnetRelativeDirArtifact =SisnetConstants.sisnetRelativeDirArtifact
    env.SONARQUBE_ENV_ID = 'SonarQubePelayo'
    env.DEPLOYIN = props.DeployIn
    env.VADDEPLOY = props.vadDeploy
    if(env.APPNAME!=CommonConstants.applications.SISNETV6){
        switch(env.VADDEPLOY.toUpperCase()){
            case["TRUE"]:
                if(env.DEPLOYIN.toUpperCase()=="BATCH")
                    env.DEPLOYVAD="false"
                else
                    env.DEPLOYVAD="true"
            break
            case["FALSE"]:
                env.DEPLOYVAD="false"
            break
            default:
                println("## WARNING: No se ha detectado un valor válido para definir si se despliega SISNETVAD, se marca la opción por defecto NO")
                env.DEPLOYVAD="false"
            break
        }
    }
}

/*
 * Mostrar la información de que se va a ejecutar
 */
def sisnetPrintStartInfo(){
    def servers
    def vadInfo=""
    println("## INFO: Deploy In-> ${env.DEPLOYIN}")
    switch(env.DEPLOYIN.toUpperCase()){
        case ["ONLINE","BATCH"]:
            servers=SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".SERVERS.findAll{it."${env.DEPLOYIN.toUpperCase()}"}
            break
        case ["BOTH"]:
            servers=SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".SERVERS
            break
    }
    if(env.DEPLOYVAD=="true"){
        vadInfo="""\n##############################################
## - Aplicación: SISNETVAD
## - Deploy VAD: Si
## - GIT
##   · Código fuente:
##     > Repositorio: ${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".URL}
##     > Branch: ${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".GIT_BRANCH}
##   · Configuración:
##     > Repositorio: ${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.URL}
##     > Branch: ${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.BRACH}"""
    }
    def serversInfo="## - Servidor/es destino:"
    servers.each{server ->
        serversInfo+="\n##   · Servidor: ($server.IP) $server.NAME"
    }
    println("""##############################################
## Información de la ejecución del pipeline ##
##############################################
## - Aplicación: SISNET
## - Entorno destino: $env.TARGET_ENVIRONMENT
## - Deploy IN: $env.DEPLOYIN
## - GIT:
##   · Código fuente
##     > Repositorio: ${env.gitURLSisnet}
##     > Branch: ${env.gitBranchSisnet}
##     > Path destino: ./${env.workspaceSisnetRelativeDirSrc}
##   · Configuración
##     > Repositorio: ${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.URL}
##     > Branch: ${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".SISNETCONFIGGIT.BRANCH}
##     > Path destino: $SisnetConstants.sisnetRelativeDirConfig
${serversInfo} ${vadInfo}
##############################################""")
}

/** Crear notificación de aviso de pipe. */
def createPipeWarningNotification() {
    // Solo lo hacemos por ahora en el entorno de acpt.
    if (env.TARGET_ENVIRONMENT.toUpperCase() != "ACPT") {
        return
    }
    echo "Creando notificación."
    String environment = ""
    switch(env.TARGET_ENVIRONMENT.toUpperCase()) {
        case "INTG":
            environment = "DES"
            break
        case "ACPT":
            environment = "ACPT"
            break
        case "PREP":
            environment = "PREP"
            break
        default:
            environment = ""
            break
    }
    
    def urlBody = "{\"artifactId\": \"SISnet\",\"domain\": \"sisnet\",\"environment\": \"${environment}\"}"

    def response = ""
    def pipeWarningEndpoint = "/command/pipe-warning-notifications"
    try {
        response = sh(returnStdout: true, script: """curl -k --request POST --url ${env.NOTIFICATION_MICRO_ENDPOINT}${pipeWarningEndpoint} -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer ${env.BEARER_TOKEN}' --data '${urlBody}'""").trim();

    } catch (Exception e) {
        println("No se ha podido realizar la petición HTTP a micro de notificación: ${e.getMessage()}")
        return false
    }
    try {
        def responseContent = new JsonSlurperClassic().parseText(response)
        println("La creación de la notificación devuelve ${responseContent}");
    } catch (groovy.json.JsonException e) {
        error("Se ha producido un fallo. La salida es ${response}");
    }
}

def sleepIfNeeded() {
    // Dejamos la aplicación esperando durante 5 min por si alguien quiere realizar una parada.
    // Solo lo hacemos por ahora en el entorno de acpt.
    if (env.TARGET_ENVIRONMENT.toUpperCase() != "ACPT") {
        return
    }
    println("Dejamos la aplicación esperando durante 5 min por si alguien quiere realizar una parada.")
    sh("sleep 300")
}