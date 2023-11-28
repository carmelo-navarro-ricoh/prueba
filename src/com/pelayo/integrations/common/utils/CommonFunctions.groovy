package com.pelayo.integrations.common.utils

import com.pelayo.integrations.common.utils.CommonConstants

def staticVars(){
        env.SISNET_V6_CONFIG_FILES_DIRECTORY = "pricing-rate-sisnetv6-config"
}

def readPomVars(props){
    //Definición del nombre de la aplicación
    if (!props.appName?.trim()) {
        error("## ERROR: El nombre de la aplicación no está definido")
    } else {
        env.APPNAME=props.appName
    }
    //Definición del repositorio sobre el que se va a hacer el despliegue //Posiblemente no hace falta
    //env.TARGET_ENVIRONMENT = props.TARGET_ENVIRONMENT

    //Definición de la ejecución del step de QA o nó.
    switch(props."feature-qa-enabled"){
        case["true"]:
            env.executeQA="true"
        break
        case["false"]:
            env.executeQA="false"
        break
        default:
            println("## WARNING: No se ha detectado un valor válido para definir si ejecuta la fase de QA, se marca la opción por defecto NO")
            env.executeQA="false"
        break
    }

	println ("El entorno es: ${env.TARGET_ENVIRONMENT}")

    switch(env.TARGET_ENVIRONMENT.toUpperCase()) {
        case ["INTG","ACPT","PREP"]:
            env.ARTIFACT_VERSION=generateArtifactVersion(env.TARGET_ENVIRONMENT)+"-SNAPSHOT"
        break
        case ["PROD"]:
            env.ARTIFACT_VERSION=generateArtifactVersion(env.TARGET_ENVIRONMENT)
        break
    }

    //Definición del parametro que describe los pasos a ejecutar
    env.DESIRED_STEP = props.desiredStep
    //Steps a realizar en el proceso de despliegue
    switch(env.DESIRED_STEP.toUpperCase()) {
        case CommonConstants.desiredSteps.COMMIT.NAME:
            env.stepInit="true"
            env.stepBuild="false"
            env.stepQA="false"
            env.stepPublish="false"
            env.stepDeploy="false"
            env.stepPostActions="false"
            env.publishWar="false"
        break
        case CommonConstants.desiredSteps.BUILD.NAME:
            env.stepInit="true"
            env.stepBuild="true"
            env.stepQA=env.executeQA
            env.stepPublish="false"
            env.stepDeploy="false"
            env.stepPostActions="false"
            env.publishWar="false"
        break
        case CommonConstants.desiredSteps.DEPLOY_ONLY_CONFIG.NAME:
            env.stepInit="true"
            env.stepBuild="false"
            env.stepQA=env.executeQA
            env.stepPublish="false"
            env.stepDeploy="true"
            env.stepPostActions="true"
            env.publishWar="false"
        break
        case CommonConstants.desiredSteps.DEPLOY.NAME:
            env.stepInit="true"
            env.stepBuild="true"
            env.stepQA=env.executeQA
            env.stepPublish="true"
            env.stepDeploy="true"
            env.stepPostActions="true"
            env.publishWar="true"
        break
        default:
            println("## WARNING: No se ha detectando un valór válido para el \"DESIRED_STEP\", es aplica la opción por defecto COMMIT.")
            env.stepInit="true"
            env.stepBuild="false"
            env.stepQA="false"
            env.stepPublish="false"
            env.stepDeploy="false"
            env.stepPostActions="false"
            env.publishWar="false"
        break
    }
    // Despliegue en prod con tag específico
    if (env.TAG_TO_BUILD) {
        env.stepInit="true"
        env.stepBuild="false"
        env.stepQA="false"
        env.stepPublish="false"
        env.stepDeploy="true"
        env.stepPostActions="true"
        env.publishWar="true"

        env.TAG = env.TAG_TO_BUILD
        env.BRANCH_NAME="master"
    }
}


def readGitVars(scmVars){
    println ("scmVars: ${scmVars}")
    env.GIT_COMMIT = scmVars.GIT_COMMIT
    env.GIT_URL=scm.getUserRemoteConfigs()[0].getUrl()
    if (env.TAG_TO_BUILD) {
        env.BRANCH_NAME="master"
    }
    env.TARGET_ENVIRONMENT=envFromBranch(env.BRANCH_NAME)
}


def printInitialVars(){
        println("""###########################################
## Información inicial para la ejecución ##
###########################################
## Parámetros iniciales
##-----------------------------------------
## - Nombre de la aplicación: ${env.APPNAME}
## - Entorno de despliegue: ${env.TARGET_ENVIRONMENT}
## - Ejecución del proceso de QA(ci.properties): ${env.executeQA}
## - Procesos a ejecutar(ci.properties): ${env.DESIRED_STEP}
## - Ruta de los archivos de configuración: ${env.SISNET_V6_CONFIG_FILES_DIRECTORY}
## - Git - Url: ${env.GIT_URL} 
## - Git - Branch: ${env.BRANCH_NAME}
## - Git - Commit message: ${env.GIT_COMMIT}
###########################################
## Fases a ejecutar
##-----------------------------------------
## - Init: ${env.stepInit=="true"?"Si":"No"}
## - Build: ${env.stepBuild=="true"?"Si":"No"}
## - QA: ${env.stepQA=="true"?"Si":"No"}
## - Publish: ${env.stepPublish=="true"?"Si":"No"}
## - Deploy ${env.stepDeploy=="true"?"Si":"No"}
## - PostActions: ${env.stepPostActions=="true"?"Si":"No"}
## - Publish WAR: ${env.publishWar=="true"?"Si":"No"}
###########################################""")
}

/**
 * Inicializa las variables comunes a todos los tipos de proyecto
 * @param props
 * @param scmVars
 * @return
 */
def initCommonsVars(props, scmVars) {
    //Verificamos si se ha proporcionado el nombre de la aplicación a gestionar
    if (!props.appName?.trim()) {
        error("## ERROR: El nombre de la aplicación no está definido")
    } else {
        env.APPNAME=props.appName
    }
    env.SONARQUBE_ENV_ID = 'SonarQubePelayo'
    env.DESIRED_STEP = props.desiredStep
    env.GIT_COMMIT = scmVars.GIT_COMMIT
    env.SISNET_V6_CONFIG_FILES_DIRECTORY = "pricing-rate-sisnetv6-config"
    env.TARGET_ENVIRONMENT = props.TARGET_ENVIRONMENT
    env.DEPLOYIN = props.DeployIn
	
	if( props.vadDeploy?.hasValues() ){
        env.VADDEPLOY=props.vadDeploy
    } else {
        env.VADDEPLOY=""
    }

    if(env.APPNAME!=CommonConstants.applications.SISNETV6){
        switch(env.VADDEPLOY.toUpperCase()){
            case["TRUE"]:
                if(env.DEPOLYIN.toUpperCase()=="BATCH")
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

    println("## INFO (tmp): ${env.TARGET_ENVIRONMENT}")
    switch(env.TARGET_ENVIRONMENT.toUpperCase()) {
        case ["INTG","ACPT","PREP"]:
            env.ARTIFACT_VERSION=generateArtifactVersion(env.TARGET_ENVIRONMENT)+"-SNAPSHOT"
        break
        case ["PROD"]:
            env.ARTIFACT_VERSION=generateArtifactVersion(env.TARGET_ENVIRONMENT)
        break
    }

    switch(props."feature-qa-enabled"){
        case["true"]:
            env.executeQA="true"
        break
        case["false"]:
            env.executeQA="false"
        break
        default:
            println("## WARNING: No se ha detectado un valor válido para definir si ejecuta la fase de QA, se marca la opción por defecto NO")
            env.executeQA="false"
        break
    }

    switch(env.DESIRED_STEP.toUpperCase()) {
        case CommonConstants.desiredSteps.COMMIT.NAME:
            env.stepInit="true"
            env.stepBuild="false"
            env.stepQA="false"
            env.stepPublish="false"
            env.stepDeploy="false"
            env.stepPostActions="false"
            env.publishWar="false"
        break
        case CommonConstants.desiredSteps.BUILD.NAME:
            env.stepInit="true"
            env.stepBuild="true"
            env.stepQA=env.executeQA
            env.stepPublish="false"
            env.stepDeploy="false"
            env.stepPostActions="false"
            env.publishWar="false"
        break
        case CommonConstants.desiredSteps.DEPLOY_ONLY_CONFIG.NAME:
            env.stepInit="true"
            env.stepBuild="false"
            env.stepQA=env.executeQA
            env.stepPublish="false"
            env.stepDeploy="true"
            env.stepPostActions="true"
            env.publishWar="false"
        break
        case CommonConstants.desiredSteps.DEPLOY.NAME:
            env.stepInit="true"
            env.stepBuild="true"
            env.stepQA=env.executeQA
            env.stepPublish="true"
            env.stepDeploy="true"
            env.stepPostActions="true"
            env.publishWar="true"
        break
        default:
            println("## WARNING: No se ha detectando un valór válido para el \"DESIRED_STEP\", es aplica la opción por defecto COMMIT.")
            env.stepInit="true"
            env.stepBuild="false"
            env.stepQA="false"
            env.stepPublish="false"
            env.stepDeploy="false"
            env.stepPostActions="false"
            env.publishWar="false"
        break
    }
}

/**
 *	Secion para envios de emails.
 */
def notifyByEmail(type,mailFrom,mailTo) {
    def envioEmail=true
    if(!type?.trim()){
        println("## INFO: No se ha definido el tipo de email a enviar. Se cancela el envio")
        envioEmail=false
    }
    if(!mailFrom?.trim()){
        mailFrom=CommonConstants.DEFAULT_MAIL_FROM
    }
    if(!mailTo?.trim()){
        mailTo=CommonConstants.DEFAULT_MAIL_TO
    }
    switch(type) {
        case "SISNET_START_PROCESS":
            mailSubject="Job execution: Job '$env.JOB_NAME [$env.BUILD_NUMBER]'"
            mailBody="""<p>Job '$env.JOB_NAME [$env.BUILD_NUMBER]':</p><p>Environment configuration for environment [$env.TARGET_ENVIRONMENT] </p>"""
            break
        default:
            println("## INFO: No se ha definido el tipo de email válido a enviar. Se cancela el envio")
            envioEmail=false
            break
    }
    if(envioEmail){
        try {
            emailext (
                subject: mailSubject,
                body: mailBody,
                from: mailFrom,
                to: mailTo
            )
        } catch (Exception ex) {
            println("## WARNING: No se ha podido enviar el corro de tipo $type a $mailTo. Se ha recibido el error: $ex")
        }
    }
}

def checkoutGit(String giturl, String branches, String relativeTargetDir){
	checkout([
        $class: 'GitSCM', 
        branches: [[name: branches]],
		extensions: [[$class: 'RelativeTargetDirectory', 
		relativeTargetDir: relativeTargetDir]],
     	userRemoteConfigs: [[url: giturl]]]
    )
}

def generateArtifactVersion(entorno){
    def now = new Date()
    def version= now.format("yy.MM.dd", TimeZone.getTimeZone('CET'))
    if(entorno!=null && entorno!="")
        version="$version-$entorno"
    return version
}

def generateArtifactVersionFullDate(){
    def now = new Date()
    //def version= now.format("yy.MM.dd", TimeZone.getTimeZone('UTC'))
    def version= now.format("yy.MM.ddHHmm", TimeZone.getTimeZone('CET'))
/*
    if(entorno!=null && entorno!="")
        version="$version-$entorno"
*/
    return version
}

//Publicar artefacto en el nexus
def artifactPublish(String nexus_url, String groupId, String artifactId, String version, String file, String filetype){
    def mvnHome = tool 'maven 3.3.9'
	def mvnExec = mvnHome + "/bin/mvn"
    println("""#######################################
## Publicación de artefacto en nexus ##
## Nexus URL: ${nexus_url}
## GroupID: ${groupId}
## Artefacto: ${artifactId}
## Versión: ${version}
## Tipo de fichero: ${filetype}
## Fichero: ${file}
## Comando:
${mvnExec} -B deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=${filetype} -DuniqueVersion=false -Dfile=${file} -Durl=${nexus_url}
#######################################""")

    sh("${mvnExec} -B deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=${filetype} -DuniqueVersion=false -Dfile=${file} -Durl=$nexus_url")
}

def initialExecutionInfo(){
    println("""###########################################
## Información inicial para la ejecución ##
###########################################
## Fases a ejecutar
## - Init: ${env.stepInit=="true"?"Si":"No"}
## - Build: ${env.stepBuild=="true"?"Si":"No"}
## - QA: ${env.stepQA=="true"?"Si":"No"}
## - Publish: ${env.stepPublish=="true"?"Si":"No"}
## - Deploy ${env.stepDeploy=="true"?"Si":"No"}
## - PostActions: ${env.stepPostActions=="true"?"Si":"No"}
## - Publish WAR: ${env.publishWar=="true"?"Si":"No"}
###########################################""")
}

/**
* Notificar a la cmdb. Si no se sabe todavia el id de la app en la cmdb entonces hay que ir a por id 
*
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @param deployStatus estado de la aplicacion una vez desplegada
* @param version version que se va a desplegar
* @param buildingBlock id del building block
*/
def notifyJobCMDB(jenkinsStatus,deployStatus,version,buildingBlock) {
    println ("## Info: El id de la app en la CMDB es ${env.CMDB_APP_ID}");
    if (env.CMDB_APP_ID.equals("")){
        env.CMDB_APP_ID = calculateAppId(scm.getUserRemoteConfigs()[0].getUrl().replaceAll(":","%3A").replaceAll("/","%2F").replaceAll("@","%40"));
    }

    if (!env.CMDB_APP_ID.equals("") ){
        notifyJobCMDBWithIdApp(jenkinsStatus,deployStatus,version,env.CMDB_APP_ID,buildingBlock)
    }else{
        println ("## WARNING: No se puede llamar a la CMDB porque no se ha encontrado el Id de la aplicación");
    }
}

def calculateURL(){
    def out = '';
    switch(env.TARGET_ENVIRONMENT) {
        case CommonConstants.CMDB_ENV_DEV:
            out = CommonConstants.SERVER_SISNET_ENPOINT_DEV;
        break;
        case CommonConstants.CMDB_ENV_ACP:
           out = CommonConstants.SERVER_SISNET_ENPOINT_ACP;
        break;
        case CommonConstants.CMDB_ENV_PRE:
           out = CommonConstants.SERVER_SISNET_ENPOINT_PRE;
        break;
        case CommonConstants.CMDB_ENV_PRO:
            out = CommonConstants.SERVER_SISNET_ENPOINT_PRO;
        break;        
    }
  return out + '/api/openapi.yaml';  
}

/*
* Convierte el nombre del job el nombre correcto por los caracteres especiales
*/
def parseJobName(jobName){
    return jobName.replaceAll("%2F","%252F");
}

/**
 * Llama al micro de CMDB para actualizar/crear en BD un job de jenkins
 * @param jenkinsStatus estado del la tarea de jenkins a registrar
 * @param deployStatus estado de la aplicacion una vez desplegada
 * @param version version que se va a desplegar
 * @param buildingBlock id del building block
 */
def notifyJobCMDBWithIdApp(jenkinsStatus,deployStatus,version,application,buildingBlock) {                 
    def cmdbEnv = env.CMDB_ENV;      
    def cmdbCredentialId = '';    
    def otherDate = new Date();
    def jenkinsInitDate = Date.parse("yyyy-MM-dd HH:mm:ss.SSS", env.JENKINS_JOB_CMDB_TIME);
    def nowDate =   Date.parse("yyyy-MM-dd HH:mm:ss.SSS", env.JENKINS_LAST_JOB_CMDB_TIME);   
    
    def duration = 0;
        
    duration = groovy.time.TimeCategory.minus(otherDate,nowDate).toMilliseconds();
    println("## INFO: Las fechas son ${otherDate.format("yyyy-MM-dd HH:mm:ss.SSS")} y ${ nowDate.format("yyyy-MM-dd HH:mm:ss.SSS")} y la dif ${duration}");

    println("## INFO: Las fecha ini ${jenkinsInitDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}");

    //actualizamos la ultima fecha de referencia
    env.JENKINS_LAST_JOB_CMDB_TIME = otherDate.format("yyyy-MM-dd HH:mm:ss.SSS");
    def jenkinsInitDateString = "${jenkinsInitDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}";

    println("## INFO: Se procede a invocar el micro de CMDB para actualizar el job ${env.BUILD_ID} en el entorno ${cmdbEnv} ...");
    
    // Recuperar token endpoint correspondiente al entorno y la credencial necesaria
    switch(cmdbEnv) {
        case CommonConstants.CMDB_ENV_DEV:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_DEV;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_DEV;
            env.NOTIFICATION_MICRO_ENDPOINT = CommonConstants.NOTIFICATION_MICRO_ENDPOINT_DEV;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_DEV;
        break;
        case CommonConstants.CMDB_ENV_ACP:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_ACP;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_ACP;
            env.NOTIFICATION_MICRO_ENDPOINT = CommonConstants.NOTIFICATION_MICRO_ENDPOINT_ACP;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_ACP;
        break;
        case CommonConstants.CMDB_ENV_PRE:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_PRE;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_PRE;
            env.NOTIFICATION_MICRO_ENDPOINT = CommonConstants.NOTIFICATION_MICRO_ENDPOINT_PRE;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_PRE;
        break;
        case CommonConstants.CMDB_ENV_PRO:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_PRO;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_PRO;
            env.NOTIFICATION_MICRO_ENDPOINT = CommonConstants.NOTIFICATION_MICRO_ENDPOINT_PRO;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_PRO;
        break;        
        default:
            env.CMDB_TOKEN_ENDPOINT = '';
            cmdbCredentialId = '';
        break;
    }

    println("## env.NOTIFICATION_MICRO_ENDPOINT: ${env.NOTIFICATION_MICRO_ENDPOINT}")

    try{
        // Obtener token
        withCredentials([string(credentialsId: cmdbCredentialId, variable: 'TOKEN')]) {
            def cmdbTokenEndpointResponse = sh(returnStdout: true, script: 'curl -k --request POST "$CMDB_TOKEN_ENDPOINT" --header "Content-Type: application/x-www-form-urlencoded" --header "Authorization: Basic $TOKEN" --data-urlencode "grant_type=client_credentials"').trim();
            def cmdbTokenProps = readJSON text: cmdbTokenEndpointResponse;
            env.BEARER_TOKEN = cmdbTokenProps.access_token;
        }

        // Invocacion del micro de cmdb
        if(env.BEARER_TOKEN != '') {

            def jenkinsPublisher = 'automatic';
            if (env.USER != null){
                jenkinsPublisher = env.USER;
            }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
            env.CMDB_REQ_BODY = "{\"environment\": \"${env.TARGET_ENVIRONMENT}\",\"id\": \"${env.JENKINS_JOB_CMDB_ID}\",\"jenkinsJobId\": \"${env.BUILD_ID}\",\"application\": \"${application}\",\"jenkinsPublisher\": \"${jenkinsPublisher}\",\"jenkinsJob\": \"${parseJobName(env.JOB_NAME)}\",\"jenkinsStatus\": \"${jenkinsStatus}\",\"deployStatus\": \"${deployStatus}\",\"deployVersion\": \"${version}\",\"jenkinsFinishDate\":\"${otherDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}\",\"jenkinsInitDate\": \"${jenkinsInitDateString}\",\"deployDate\":\"${otherDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}\",\"buildingBlocks\": [{\"name\": \"${buildingBlock}\",\"duration\": ${duration}}],\"branch\":\"${env.BRANCH_NAME}\",\"deployUrl\":\"${calculateURL()}\"}";
            env.CMDB_MICRO_ENDPOINT = CMDB_MICRO_ENDPOINT + "jenkins" + env.JENKINS_JOB_CMDB_URL;

            println("## INFO: Realizando peticion al micro ${CMDB_MICRO_ENDPOINT}");
            microResponse = sh(returnStdout: true, script:  '''
                set +x
                curl -g -k --request "$JENKINS_JOB_CMDB_OPERATION" "$CMDB_MICRO_ENDPOINT" -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer $BEARER_TOKEN" --data "$CMDB_REQ_BODY"
                set -x
                ''').trim();
            println("DEBUG: ${microResponse}");
            def cmdbMicroProps = readJSON text: microResponse;
            def cmdbMicroRes = cmdbMicroProps.jenkinsJobId;
            if(cmdbMicroRes != "") {
                println("## INFO: El jenkins job con jobId ${cmdbMicroRes} se actulizo correctamente");
            } else {
                println("## ERROR: No se ha podido actualizar el jenkins job de cmdb ${$cmdbMicroRes}.");
            }

        } else {
            println("## ERROR: No se ha podido obtener token para invocar al micro de CMDB");
        }

        // modificamos porque las siguientes van a ser modificaciones
        env.JENKINS_JOB_CMDB_OPERATION = "PUT";  
        env.JENKINS_JOB_CMDB_URL = "/" + env.JENKINS_JOB_CMDB_ID  ;  
    }catch(hudson.AbortException | net.sf.json.JSONException ej){
        println("## ERROR: Se ha producido un fallo al llamar a la CMDB, no se registrara el avance del proceso" + ej);
    }
}

/**
* Obtiene el id de la app de la CMDB a partir del repo de bitbucket
* @Param repo repositorio del bitbucket
*/
def calculateAppId(repo) {
    def cmdbEnv = env.CMDB_ENV;    
    def cmdbMicroRes = "";
    def cmdbCredentialId = '';
    
    println("## INFO: Se procede a invocar el micro de CMDB para obtener el id de la aplicacion en el entorno ${cmdbEnv} ...");

    // Recuperar token endpoint correspondiente al entorno y la credencial necesaria
    switch(cmdbEnv) {
        case CommonConstants.CMDB_ENV_DEV:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_DEV;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_DEV;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_DEV;
            env.CMDB_MICRO_ENDPOINT = CMDB_MICRO_ENDPOINT + "cmdb-applications?repo=${repo}&unPaged=true&page=0&size=20";
        break;
        case CommonConstants.CMDB_ENV_ACP:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_ACP;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_ACP;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_ACP;
            env.CMDB_MICRO_ENDPOINT = CMDB_MICRO_ENDPOINT + "cmdb-applications?repo=${repo}&unPaged=true&page=0&size=20";
        break;
        case CommonConstants.CMDB_ENV_PRE:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_PRE;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_PRE;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_PRE;
            env.CMDB_MICRO_ENDPOINT = CMDB_MICRO_ENDPOINT + "cmdb-applications?repo=${repo}&unPaged=true&page=0&size=20";
        break;
        case CommonConstants.CMDB_ENV_PRO:
            env.CMDB_TOKEN_ENDPOINT = CommonConstants.WSO2_TOKEN_ENDPOINT_PRO;
            env.CMDB_MICRO_ENDPOINT = CommonConstants.CMDB_MICRO_WSO2_ENPOINT_PRO;
            cmdbCredentialId = CommonConstants.WSO2_JENKINS_CRED_ID_PRO;
            env.CMDB_MICRO_ENDPOINT = CMDB_MICRO_ENDPOINT + "cmdb-applications?repo=${repo}&unPaged=true&page=0&size=20";
        break;        
        default:
            env.CMDB_TOKEN_ENDPOINT = '';
            cmdbCredentialId = '';
        break;
    }
    try{
        // Obtener token
        withCredentials([string(credentialsId: cmdbCredentialId, variable: 'TOKEN')]) {
            def cmdbTokenEndpointResponse = sh(returnStdout: true, script: 'curl -k --request POST "$CMDB_TOKEN_ENDPOINT" --header "Content-Type: application/x-www-form-urlencoded" --header "Authorization: Basic $TOKEN" --data-urlencode "grant_type=client_credentials"').trim();
            def cmdbTokenProps = readJSON text: cmdbTokenEndpointResponse;
            env.BEARER_TOKEN = cmdbTokenProps.access_token;
        }
        // Invocacion del micro de cmdb
        if(env.BEARER_TOKEN != '') {
            println("## INFO: Realizando peticion al micro ${CMDB_MICRO_ENDPOINT}");
            microResponse = sh(returnStdout: true, script:  '''
                set +x
                curl -g -k --request GET "$CMDB_MICRO_ENDPOINT" -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer $BEARER_TOKEN" 
                set -x
                ''').trim();
            println("## DEBUG: ${microResponse}");
            def cmdbMicroProps = readJSON text: microResponse;

            if (cmdbMicroProps.totalElements > 0 ){
                cmdbMicroRes = cmdbMicroProps.content[0].id;
                env.IS_FORBIDDEN_INTG = cmdbMicroProps.content[0].forbiddenDeployIntg;
                env.IS_FORBIDDEN_ACPT = cmdbMicroProps.content[0].forbiddenDeployAcpt;
                env.IS_FORBIDDEN_PRE = cmdbMicroProps.content[0].forbiddenDeployPre;
                println("IS_FORBIDDEN_INTG: ${IS_FORBIDDEN_INTG}")
                println("IS_FORBIDDEN_ACPT: ${IS_FORBIDDEN_ACPT}")
                println("IS_FORBIDDEN_PRE: ${IS_FORBIDDEN_PRE}")
                if(cmdbMicroRes != "") {
                    println("## INFO: La app tiene el id ${cmdbMicroRes}");
                } else {
                    println("## ERROR:  No se ha podido el id de la app.");
                }
            }else{
                println("## ERROR:  No hay app con dicho repositorio.");
            }
        } else {
            println("## ERROR:  No se ha podido obtener token para invocar al micro de CMDB");
        }
     }catch(hudson.AbortException | net.sf.json.JSONException ej){
        println("## ERROR: Se ha producido un fallo al llamar a la CMDB, no se registrara el avance del proceso" + ej);
    }
    return cmdbMicroRes;
}

def gitTag(gitVersion){
    // Asignar tags al repositorio.
    sh "git tag ${gitVersion}";
    sh "git push origin ${gitVersion}";
}

//Descargar el artefacto del nexus
def downloadArtifactFromNexus(nexusURL,nexusRepository,nexusGroupID,artifactName,version,fileName,outputPath,outputFile) {
    def nexusFullUrl="${nexusURL}${nexusRepository}${nexusGroupID}/${artifactName}/${version}/${fileName}"
    def validResponseCodes=[200,201,202]
    def response
    //Verifica si existe la ruta de destino del artefacto y si no existe, la crea
    if(!fileExists(outputPath)){
        sh("mkdir -p ${outputPath}")
    }
    try{
        //Descarga el artefacto del nexus
        response = httpRequest(
            url:nexusFullUrl,
            ignoreSslErrors:true,
            outputFile:"${outputPath}/${outputFile}",
            validResponseCodes:"100:599"
        )
    } catch (Exception e) {
        error("## ERROR: Error no especificado al descargar el artefacto\n${e}")
    }
    //Verifica que se haya descargado correctamente el artefacto
    if(!validResponseCodes.contains(response.getStatus())){
        error("## ERROR: No se ha podido descargar el artefacto de la ruta:\n${nexusFullUrl}\nCódigo de respuesta [${response.getStatus()}] siendo válidos los valores ${validResponseCodes}")
    }
}

//Ejecución del Dependency check de OWASP
def dependencyCheckExecute(srcPath=".",mavenVersion="3.3.9",owaspVersion="7.1.1",outputFormat="html,json"){
    def startDate=new Date()
    configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
        sh("mvn org.owasp:dependency-check-maven:${owaspVersion}:check -Dformats=${outputFormat} -gs ${MAVEN_SETTINGS_XML}")
    }
    def finishDate=new Date()
    def duration=finishDate.getTime()-startDate.getTime()
    duration=Math.round(duration/1000)
    notifyQADependencycmdb(env.CMDB_ENV,env.ARTIFACT_VERSION,"OK","OWASP",duration)
}

/**
* Notificar en la  cmdb una vez que se ejecute el dependency check
*/
def notifyQADependencycmdb(cmdbEnv,version,status,type,duration){
    def nowDate = new Date();

    if (env.CMDB_APP_ID.equals("")){
        env.CMDB_APP_ID = calculateAppId(scm.getUserRemoteConfigs()[0].getUrl().replaceAll(":","%3A").replaceAll("/","%2F").replaceAll("@","%40"));
    }
    if (!env.CMDB_APP_ID.equals("")){
        println ("## INFO: El id de la app en la CMDB es ${env.CMDB_APP_ID}");
        try{
            // Obtener token
            fillBearerToken (cmdbEnv);
            // Invocacion del micro de cmdb
            if(env.BEARER_TOKEN != '') {
                env.CMDB_REQ_BODY = "{\"appId\":\"${env.CMDB_APP_ID}\",\"env\":\"${cmdbEnv}\",\"version\":\"${version}\",\"type\":\"${type}\",\"duration\":${duration},\"publishDate\":\"${nowDate.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}\",\"branch\":\"origin/${env.BRANCH_NAME}\",\"status\": \"${status}\",\"idcontent\":\"Contenido del ID\"}"
                env.CMDB_MICRO_ENDPOINT = CMDB_MICRO_ENDPOINT + "cmdb-security" ;
                println("Realizando peticion al micro ${CMDB_MICRO_ENDPOINT} con ${CMDB_REQ_BODY}");
                microResponse = sh(returnStdout: true, script:  '''
                    set +x
                    curl -g -k --request POST "$CMDB_MICRO_ENDPOINT" -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer $BEARER_TOKEN" --data "$CMDB_REQ_BODY"
                    set -x
                    ''').trim();
                println("DEBUG: ${microResponse}");
                def cmdbMicroProps = readJSON text: microResponse;
                def cmdbMicroRes = cmdbMicroProps.id;
                if(cmdbMicroRes != "") {
                    println("El log con id ${cmdbMicroRes} se actulizo correctamente");
                } else {
                    println("Error. No se ha podido actualizar el log de cmdb ${$cmdbMicroRes}.");
                }
            } else {
                println("Se ha producido un error: No se ha podido obtener token para invocar al micro de CMDB");
            }
        }catch(hudson.AbortException | net.sf.json.JSONException ej){
            println("Se ha producido un fallo al llamar a la CMDB, no se registrara el avance del proceso" + ej);
        }
    }else{
        println ("## INFO: No se puede llamar a la CMDB porque no se ha encontrado el Id de la aplicación");
    }
}

/*
## Notificaciones a la CMDB.
## Valores válidos (no son keySensitive)
## - type: 
##   · SECURITY
## - environment
##   · INTG
##   · ACPT
##   · PREP
##   · PROD
*/
def cmdbPrepare(type,environment,status){
    if (env.CMDB_APP_ID.equals("")){
        env.CMDB_APP_ID = calculateAppId(scm.getUserRemoteConfigs()[0].getUrl().replaceAll(":","%3A").replaceAll("/","%2F").replaceAll("@","%40"));
    }
    switch(type.toUpperCase()) {
        case "SECURITY":
            println("## INFO: Se procede a realizar una notificación al la CMDB opción de ${type}")
            env.CMDB_REQ_BODY = "{\"appId\":\"${env.CMDB_APP_ID}\",\"env\":\"${environment}\",\"version\":\"${env.ARTIFACT_VERSION}\",\"type\":\"OWASP\",\"duration\":${env.duration},\"publishDate\":\"${new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")}\",\"status\":\"${status}\",\"branch\":\"${env.BRANCH_NAME}\",\"idcontent\":\"Contenido\"}"
            env.CMDB_MICRO_ENDPOINT = CommonConstants.cmdbInfo."${environment}".CMDBURL + "cmdb-security"
            //Llamada al micro de la CMDB
            def executeReturn = cmdbNotify(env.CMDB_MICRO_ENDPOINT,env.CMDB_REQ_BODY,env.TARGET_ENVIRONMENT)
        break
        default:
            error("## ERROR: No se ha definido un tipo de notificación a la CMDB válida")
        break
    }
}

def cmdbNotify(cmdbUrl,cmdbBody,environment){
    if(cmdbUrl=="" || cmdbUrl==null){
        println "## WARNING: No se ha especificado la URL de acceso a la CMDB"
        return false
    }
    if(cmdbBody=="" || cmdbBody==null){
        println "## WARNING: No se han especificado los valores a enviar a la CMDB"
        return false
    }
    if(environment=="" || environment==null){
        println "## WARNING: No se han especificado el entorno válido para la inclusión de la información en la CMDB"
        return false
    }
    println("""#######################################################
## Envio de información a la CMDB
#######################################################
## Entorno: ${environment}
## URL: ${cmdbUrl}
## Json de parametrización: 
${cmdbBody}
-------------------------------------------------------
## Comando a ejecutar:
curl -g -k --request POST "$cmdbUrl" -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer XXXXXXXXXXXXX" --data "$cmdbBody"
#######################################################""")
    try{
        // Obtener token de la CMDB
        def bearerToken=fillBearerToken (environment);
        // Invocacion del micro de cmdb
        //def microResponse = sh(returnStdout: true, script: "curl -g -k --request POST '$cmdbUrl' -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer $bearerToken' --data '$cmdbBody'").trim();
        def microResponse = sh(returnStdout: true, script: """
        curl -g -k --request POST '$cmdbUrl' -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer $bearerToken' --data '$cmdbBody'
        """).trim();
        
        println("DEBUG: ${microResponse}");
        def cmdbMicroProps = readJSON text: microResponse;
        def cmdbMicroRes = cmdbMicroProps.id;
        if(cmdbMicroRes != "") {
            println("El log con id ${cmdbMicroRes} se actulizo correctamente");
        } else {
            println("Error. No se ha podido actualizar el log de cmdb ${$cmdbMicroRes}.");
        }
    }catch(e){
        println("## WARNING: Se ha producido un error al realizar la notificación en la CMDB, no se ha realizado dicha operación")
        return false
    }
    return true
}

/**
* Rellenar la varibale global env.BEARER_TOKEN con un token valido o vaciar la varibale
* @param  cmdbEnv entorno donde esta la cmdb
*/
def fillBearerToken (cmdbEnv){
    def token = false;
    env.BEARER_TOKEN = '';
    // Recuperar token endpoint correspondiente al entorno y la credencial necesaria
    withCredentials([string(credentialsId: CommonConstants.cmdbInfo."${cmdbEnv}".CREDENTIAL, variable: 'TOKEN')]) {
        def cmdbTokenEndpointResponse = sh(returnStdout: true, script: "curl -k --request POST \"${CommonConstants.cmdbInfo."${cmdbEnv}".TOKENURL}\" --header \"Content-Type: application/x-www-form-urlencoded\" --header \"Authorization: Basic ${TOKEN}\" --data-urlencode \"grant_type=client_credentials\"").trim();
        def cmdbTokenProps = readJSON text: cmdbTokenEndpointResponse;
        token=cmdbTokenProps.access_token;
    }
    return token
}

//Definición del nombre del artefacto
def initArtifactVersion() {
    switch(env.TARGET_ENVIRONMENT)
        {
        case ["INTG","ACPT","PREP"]:
            env.ARTIFACT_VERSION+="-${env.TARGET_ENVIRONMENT}-SNAPSHOT"
            break
        }
    println("Versión artefacto: ${env.ARTIFACT_VERSION}")
}

//Función que verifica si existe un branch en el repositorio de git (antes de clonarlo)
def checkRemoteBranchExist(gitUrl,branch,debug=true){
    if(gitUrl=="" || gitUrl==null){
        println("## WARNING: No se ha especificado una URL para el repositorio de GIT")
        return false
    }
    if(branch=="" || branch==null){
        println("## WARNING: No se ha especificado un BRANCH para el repositorio de GIT")
        return false
    }
    def response
    try {
        response=sh(returnStdout: true, script: "git ls-remote ${gitUrl}").trim()
    } catch (Exception ex) {
        println("## WARNING: Se ha producido un error al acceder a la inforamción del repositorio ${gitUrl}")
        return false
    }
    if(!response.contains("refs/heads/${branch}")){
        if(debug){
            println("## WARNING: El branch ${branch} no está definido en el repositorio ${gitUrl}")
        }
        return false
    }
    return true
}

//Obtención del entorno de destino del despliegue en función del branch que se esté ejecutando.
def envFromBranch(branch){
    def salida=""
    switch(branch.toUpperCase()) {
        case ["ENV/01-INTG"]:
            salida="INTG"
        break
        case ["ENV/02-ACPT"]:
            salida="ACPT"
        break
        case ["ENV/03-PREP"]:
            salida="PREP"
        break
        case ["MASTER"]:
            salida="PROD"
        break
        default:
            salida=false
        break
    }
    return salida
}

//Lectura del archivo de propiedades de la BBDD
def readDBProperties(filePath = "./platform", fileName = "db.properties.json"){
    def valores
    try {
        valores=readJSON file: "${filePath}/${fileName}"
    } catch (Exception e) {
        println("## INFO: El archivo ${fileName} no existe o no es un json válido. Se incluye el valor por defecto (sin repositorios de BBDD)")
        valores=[]
    }
    return valores
}

//Lectura del archivo de información de los scripts (sql y xml) que se han de ejecutar en el proceso
def readDBScripts(filePath = "./db_maintenance", fileName = "db_maintenance.json"){
    def valores
    def borrar=readJSON file: "${filePath}/${fileName}"
    try {
        valores=readJSON file: "${filePath}/${fileName}"
    } catch (Exception e) {
        println("## INFO: El archivo ${fileName} no existe o no es un json válido. Se incluye el valor por defecto (sin listado de archivos a procesar)")
        valores=[]
    }
    return valores
}
