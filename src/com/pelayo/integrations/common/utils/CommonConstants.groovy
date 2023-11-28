package com.pelayo.integrations.common.utils

public class CommonConstants {


    public static final String SERVER_SISNET_ENPOINT_DEV = 'http://192.6.4.109:8081/sisnet';
    public static final String SERVER_SISNET_ENPOINT_ACP = 'http://360.acpt.pelayo.com/sisnet';
    public static final String SERVER_SISNET_ENPOINT_PRE = 'http://360.pre.pelayo.com/sisnet';
    public static final String SERVER_SISNET_ENPOINT_PRO = 'http://360.pelayo.com/sisnet';
	   
    public static final String ENV_DEV = 'des';
    public static final String ENV_ACP = 'acpt';
    public static final String ENV_PRE = 'pre';
    public static final String ENV_PRO = 'pro';   

    public static final String CMDB_ENV_DEV = 'INTG';
    public static final String CMDB_ENV_ACP = 'ACPT';
    public static final String CMDB_ENV_PRE = 'PREP';
    public static final String CMDB_ENV_PRO = 'PROD'; 

    public static final String ENV_PRO_FRONTTOMPRO1 = 'fronttompro1.pelayo.com';
    public static final String ENV_PRO_FRONTTOMPRO2 = 'fronttompro2.pelayo.com';
    public static final String ENV_PRO_FRONTTOMPRO3 = 'fronttompro3.pelayo.com';
    public static final String ENV_PRO_FRONTTOMPRO4 = 'fronttompro4.pelayo.com';

    public static final String WSO2_JENKINS_CRED_ID_DEV = 'wso2-dev-cred-integ-devops-cmdb';
    public static final String WSO2_JENKINS_CRED_ID_ACP = 'wso2-acp-cred-integ-devops-cmdb';
    public static final String WSO2_JENKINS_CRED_ID_PRE = 'wso2-pre-cred-integ-devops-cmdb';
    public static final String WSO2_JENKINS_CRED_ID_PRO = 'wso2-pro-cred-integ-devops-cmdb';

    public static final String CMDB_MICRO_WSO2_ENPOINT_DEV = 'https://manager.desa.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/';
    public static final String CMDB_MICRO_WSO2_ENPOINT_ACP = 'https://manager.acpt.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/';
    public static final String CMDB_MICRO_WSO2_ENPOINT_PRE = 'https://manager.prep.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/';
    public static final String CMDB_MICRO_WSO2_ENPOINT_PRO = 'https://manager.prod.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/';

    public static final String WSO2_TOKEN_ENDPOINT_DEV = 'https://manager.desa.pelayo.com/token';
    public static final String WSO2_TOKEN_ENDPOINT_ACP = 'https://manager.acpt.pelayo.com/token';
    public static final String WSO2_TOKEN_ENDPOINT_PRE = 'https://manager.prep.pelayo.com/token';
    public static final String WSO2_TOKEN_ENDPOINT_PRO = 'https://manager.prod.pelayo.com/token';

    public static final String NOTIFICATION_MICRO_ENDPOINT_DEV = 'https://manager.desa.pelayo.com/integ/devops/dashboard-notifications-srv/api/v1';
    public static final String NOTIFICATION_MICRO_ENDPOINT_ACP = 'https://manager.acpt.pelayo.com/integ/devops/dashboard-notifications-srv/api/v1';
    public static final String NOTIFICATION_MICRO_ENDPOINT_PRE = 'https://manager.pre.pelayo.com/integ/devops/dashboard-notifications-srv/api/v1';
    public static final String NOTIFICATION_MICRO_ENDPOINT_PRO = 'https://api.pelayo.com/integ/devops/dashboard-notifications-srv/api/v1';

    //EMail notifications
    public static final String DEFAULT_MAIL_FROM = "jenkins@pelayo.com"
    public static final String DEFAULT_MAIL_TO = "gnieto@pelayo.com"

	public static final Map stages=[
		"INIT":"Init",
		"BUILD":"Build / Test / Package",
		"QA":"QA",
		"PUBLISH":"Publish",
		"DEPLOY":"Deploy",
		"POSTACTIONS":"Post Actions"
	]
	public static final Map applications=[
		"SISNET":"SISNET",
		"SISNETV6":"SISNETV6",
		"SISNETVAD":"SISNETVAD"
	]
	public static final Map desiredSteps=[
		"COMMIT":["NAME":"COMMIT","DESCRIPTION":"NO ejecuta ninguna fase."],
		"BUILD":["NAME":"BUILD","DESCRIPTION":"Ejecución parcial, se ejecutan las fases INIT, BUILD y QA"],
		"DEPLOY_ONLY_CONFIG":["NAME":"DEPLOY_ONLY_CONFIG","DESCRIPTION":"Ejecución parcial, se ejecutan todas las fases pero no se hace el deploy del WAR"],
		"DEPLOY":["NAME":"DEPLOY","DESCRIPTION":"Ejecución completa, se ejecutan todas las fases INIT, BUILD, QA, PUBLISH, DEPLOY y POSTACTIONS"]
	]

	public static final Map cmdbInfo=[
        "INTG":["TOKENURL":"https://manager.desa.pelayo.com/token","CMDBURL":"https://manager.desa.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/","CREDENTIAL":"wso2-dev-cred-integ-devops-cmdb"],
        "ACPT":["TOKENURL":"https://manager.acpt.pelayo.com/token","CMDBURL":"https://manager.acpt.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/","CREDENTIAL":"wso2-acp-cred-integ-devops-cmdb"],
        "PREP":["TOKENURL":"https://manager.prep.pelayo.com/token","CMDBURL":"https://manager.prep.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/","CREDENTIAL":"wso2-pre-cred-integ-devops-cmdb"],
        "PROD":["TOKENURL":"https://manager.prod.pelayo.com/token","CMDBURL":"https://manager.prod.pelayo.com/integ/devops/dashboard-cmdb-application-srv/api/v1/","CREDENTIAL":"wso2-pro-cred-integ-devops-cmdb"]
    ]
    public static final Map workingModesEnum=[
        "MANUAL_TAG":["NAME":"MANUAL_TAG","DESCRIPTION":"Desplieuge manual"],
        "WEBHOOK":["NAME":"WEBHOOK","DESCRIPTION":"Desplieuge automático"]
    ]
}
