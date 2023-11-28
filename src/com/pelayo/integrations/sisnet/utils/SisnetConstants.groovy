package com.pelayo.integrations.sisnet.utils

public class SisnetConstants {
    public static final String sisnetBasePath = "sisnet";
    public static final String sisnetGitUrl = 'ssh://bitbucket-lab.pelayo.com:7999/snt/sisnet.git';
    public static final String sisnetRelativeDirSrc = "$sisnetBasePath/src";
    public static final String sisnetRelativeDirConfig = "$sisnetBasePath/config";
	public static final String uriMavenFiles = "$sisnetRelativeDirConfig/maven/";
    public static final String uriConfigFiles = "$sisnetRelativeDirConfig/producto/";
    public static final String sisnetRelativeDirArtifact = "$sisnetBasePath/artifact";

    public static final Map artifacts = [
        "INTEGRACIONES":"IntegracionesPelayo",
        "SISNET":"SISnetPelayo",
        "SISNETVAD":"SISnetPelayoVAD",
        "SISNETWAR":"WebSISnetPelayo"
        ];

	// Listado base de los nombre de los archivos de configuración
	public static final String[] productoFicherosConfiguracionBase = [
        "jobparms.xml",
        "sisnet_sismemory.xml",
        "SISnetServiciosExternos.xml",
        "SISnetRESTConfig.xml",
        "SISnetRedireccionExterna.xml",
        "oim.properties",
        "sisnet.xml",
        "sisnet_rutas_publicadas.xml",
        "sisnet_multis.xml",
        "aplicaciones.xml",
        "sisnet_entorno.xml",
        "SISnetSocketConfig.xml"
        ];

	// Listado base de los nombre de los archivos de configuración en producción
    public static final String[] productoFicherosConfiguracionProd = [
        "jobparms.xml",
        "sisnet_sismemory.xml",
        "SISnetServiciosExternos.xml",
        "SISnetRESTConfig.xml",
        "SISnetRedireccionExterna.xml",
        "oim.properties",
        "sisnet_rutas_publicadas.xml",
        "sisnet_multis.xml",
        "aplicaciones.xml",
        "sisnet_entorno.xml",
        "SISnetSocketConfig.xml"
        ];

    public static final String[] ficherosConfiguracionCompilacionModulos = [
        'WebSISnetPelayo/pom.xml',
        'SISnetPelayo/pom.xml',
        'SISnetPelayoVAD/pom.xml',
        'IntegracionesPelayo/pom.xml'
        ];

	public static final String gitBBDDRepo="ssh://git@bitbucket-lab.pelayo.com:7999/snt/sisnet-bdd.git";

    // Información de los entornos de SISNET
    public static Map ENTORNOS_SISNET=[
        "INTG":[
            "NAME":"Integración",
            "VERSION_PATH":"02-INTG",
            "GIT_PATH":"SISnet/02-INTG",
            "GIT_BRANCH":"*/env/01-INTG",
            "POM_PATH":"maven/02-INTG",
            "USER":"operador",
            "COMANDOS_INICIALES_DESPLIEGUE":[
                "./stopTomcat.sh", 
                "sleep 10"
                ],
            "COMANDOS_LIMPIEZA_DESPLIEGUE":[
                "rm -rf /netijam/apache-tomcat/webapps/sisnet.war", 
                "rm -rf /netijam/apache-tomcat/webapps/sisnet/", 
                "rm -rf /netijam/apache-tomcat/work/Catalina/localhost/sisnet/"
                ],
            "COMANDOS_FINALES_DESPLIEGUE":[
                "./arranca_maestro"
            ],
            "SERVERS":[
                ["NAME":"maestrodesa_hog","IP":"maestrodesa_hog","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestrodesa_hog"]
            ],
            "REMOTE_DEPLOY_PATH":'/netijam/WorkSISnet/Configuracion/',
            "CONFIG_FILE_LIST":productoFicherosConfiguracionBase,
            "ARTIFACTREPOSITORY":[
                "URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/pelayo-sisnet-snapshots/",
                "GROUPID":"org.pelayo"
            ],
            "SISNETCONFIGGIT":[
                "URL":'ssh://git@bitbucket-lab.pelayo.com:7999/snt/config-sisnet.git',
	            "BRANCH": 'master'
            ],
            "XMLCOMPLEJO":[
                "URL":"http://192.6.4.83:8081/sisnet/api/alm/1/importar/complejo",
                "ACTIVE":true
            ],
            "CMDB":[
                "DBSCRIPTS":[
                    "URL":"http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file"
                ]
            ]
        ],
        "ACPT":[
            "NAME":"Aceptación",
            "VERSION_PATH":"03-ACPT",
            "GIT_PATH":"SISnet/03-ACPT",
            "GIT_BRANCH":"*/env/02-ACPT",
            "POM_PATH":"maven/03-ACPT",
            "USER":"operador",
            "COMANDOS_INICIALES_DESPLIEGUE":[
                "./stopTomcat.sh", 
                "sleep 10"
                ],
            "COMANDOS_LIMPIEZA_DESPLIEGUE":[
                "rm -rf /netijam/apache-tomcat/webapps/sisnet.war", 
                "rm -rf /netijam/apache-tomcat/webapps/sisnet/", 
                "rm -rf /netijam/apache-tomcat/work/Catalina/localhost/sisnet/"
                ],
            "COMANDOS_FINALES_DESPLIEGUE":[
                "./arranca_maestro"
            ],
            "SERVERS":[
                ["NAME":"maestroacpt_hog","IP":"maestroacpt_hog","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestroacpt_hog"],
                ["NAME":"maestroacptbatch","IP":"maestroacptbatch","ONLINE":false,"BATCH":true,"USRHOST":"operador@maestroacptbatch"]
            ],
            "REMOTE_DEPLOY_PATH":'/netijam/WorkSISnet/Configuracion/',
            "CONFIG_FILE_LIST":productoFicherosConfiguracionBase,
            "ARTIFACTREPOSITORY":[
                "URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/pelayo-sisnet-snapshots/",
                "GROUPID":"org.pelayo"
            ],
            "SISNETCONFIGGIT":[
                "URL":'ssh://git@bitbucket-lab.pelayo.com:7999/snt/config-sisnet.git',
	            "BRANCH": 'master'
            ],
            "XMLCOMPLEJO":[
                "URL":"http://192.6.4.83:8081/sisnet/api/alm/1/importar/complejo",
                "ACTIVE":true
            ],
            "CMDB":[
                "DBSCRIPTS":[
                    "URL":"http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file"
                ]
            ]
        ],
        "PREP":[
            "NAME":"Preproducción",
            "VERSION_PATH":"04-PREP",
            "GIT_PATH":"SISnet/04-PREP",
            "GIT_BRANCH":"*/env/03-PREP",
            "POM_PATH":"maven/04-PREP",
            "USER":"operador",
            "COMANDOS_INICIALES_DESPLIEGUE":[
                "./stopTomcat.sh", 
                "sleep 10"
                ],
            "COMANDOS_LIMPIEZA_DESPLIEGUE":[
                "rm -rf /netijam/apache-tomcat/webapps/sisnet.war",
                "rm -rf /netijam/apache-tomcat/webapps/sisnet/",
                "rm -rf /netijam/apache-tomcat/work/Catalina/localhost/sisnet/"
                ],
            "COMANDOS_FINALES_DESPLIEGUE":[
                "./arranca_maestro"
            ],
            "SERVERS":[
                ["NAME":"maestropre","IP":"maestropre","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropre"],
                ["NAME":"maestropre2","IP":"maestropre2","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropre2"]
            ],
            "REMOTE_DEPLOY_PATH":'/netijam/WorkSISnet/Configuracion/',
            "CONFIG_FILE_LIST":productoFicherosConfiguracionBase,
            "ARTIFACTREPOSITORY":[
                "URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/pelayo-sisnet-snapshots/",
                "GROUPID":"org.pelayo"
            ],
            "SISNETCONFIGGIT":[
                "URL":'ssh://git@bitbucket-lab.pelayo.com:7999/snt/config-sisnet-pro.git',
	            "BRANCH": 'master'
            ],
            "XMLCOMPLEJO":[
                "URL":"http://192.6.4.109:8081/sisnet/api/alm/1/importar/complejo",
                "ACTIVE":false
            ],
            "CMDB":[
                "DBSCRIPTS":[
                    "URL":"http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file"
                ]
            ]
        ],
        "PROD":[
            "NAME":"Producción",
            "VERSION_PATH":"05-PROD",
            "GIT_PATH":"SISnet/05-PROD",
            "GIT_BRANCH":"*/master",
            "POM_PATH":"maven/05-PROD",
            "USER":"arquitec",
            "COMANDOS_INICIALES_DESPLIEGUE":[
                "./stopTomcat.sh", 
                "sleep 10"
                ],
            "COMANDOS_LIMPIEZA_DESPLIEGUE":[
                "rm -rf /netijam/apache-tomcat-8.0.33/webapps/sisnet.war", 
                "rm -rf /netijam/apache-tomcat-8.0.33/webapps/sisnet/", 
                "rm -rf /netijam/apache-tomcat-8.0.33/work/Catalina/localhost/sisnet/"
                ],
            "COMANDOS_FINALES_DESPLIEGUE":[
                "./arranca_maestro"
            ],
            "SERVERS":[
                ["NAME":"ProBatch","IP":"maestrobatch","ONLINE":false,"BATCH":true,"USRHOST":""],
                ["NAME":"maestropro1","IP":"maestropro1","ONLINE":false,"BATCH":true,"USRHOST":""],
                ["NAME":"maestropro2","IP":"maestropro2","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro2"],
                ["NAME":"maestropro3","IP":"maestropro3","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro3"],
                ["NAME":"maestropro4","IP":"maestropro4","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro4"],
                ["NAME":"maestropro5","IP":"maestropro5","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro5"],
                ["NAME":"maestropro6","IP":"maestropro6","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro6"],
                ["NAME":"maestropro7","IP":"maestropro7","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro7"],
                ["NAME":"maestropro8","IP":"maestropro8","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro8"],
                ["NAME":"maestropro9","IP":"maestropro9","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro9"],
                ["NAME":"maestropro10","IP":"maestropro10","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro10"],
                ["NAME":"maestropro11","IP":"maestropro11","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro11"],
                ["NAME":"maestropro12","IP":"maestropro12","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro12"],
                ["NAME":"maestropro13","IP":"maestropro13","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro13"],
                ["NAME":"maestropro14","IP":"maestropro14","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro14"],
                ["NAME":"maestropro15","IP":"maestropro15","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro15"],
                ["NAME":"maestropro16","IP":"maestropro16","ONLINE":true,"BATCH":false,"USRHOST":"operador@maestropro16"]
            ],
            "REMOTE_DEPLOY_PATH":'/netijam/WorkSISnet/Configuracion/',
            "CONFIG_FILE_LIST":productoFicherosConfiguracionProd,
            "ARTIFACTREPOSITORY":[
                "URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/pelayo-sisnet-releases/",
                "GROUPID":"org.pelayo"
            ],
            "SISNETCONFIGGIT":[
                "URL":'ssh://git@bitbucket-lab.pelayo.com:7999/snt/config-sisnet-pro.git',
	            "BRANCH": 'master'
            ],
            "XMLCOMPLEJO":[
                "URL":"http://192.6.4.67:8081/sisnet/api/alm/1/importar/complejo",
                "ACTIVE":false
            ],
            "CMDB":[
                "DBSCRIPTS":[
                    "URL":"http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file"
                ]
            ]
        ]
    ]

}