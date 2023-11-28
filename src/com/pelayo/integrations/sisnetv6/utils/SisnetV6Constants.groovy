package com.pelayo.integrations.sisnetv6.utils

public class SisnetV6Constants {
	public static final Map projectTypes=[
        "MAVEN_WAR_SISNET_V6":"MAVEN_WAR_SISNET_V6",
        "MAVEN_WAR_SISNET":"MAVEN_WAR_SISNET",
        "MAVEN_WAR_BFF":"MAVEN_WAR_BFF",
        "MAVEN_SERVICE":"MAVEN_SERVICE",
        "MAVEN_LIBRARY":"MAVEN_LIBRARY",
        "NODE_PACKAGE":"NODE_PACKAGE",
        "ANGULAR_APP":"ANGULAR_APP"
	]

	public static final Map branchesName=[
        "INTG":"env/01-INTG",
        "ACPT":"env/02-ACPT",
        "PREP":"env/03-PREP",
        "PROD":"master"
    ]
	public static final Map environments=[
        "INTG":["TARGET":"INTG","CONFIG":"02-INTG"],
        "ACPT":["TARGET":"ACPT","CONFIG":"03-ACPT"],
        "PREP":["TARGET":"PREP","CONFIG":"04-PREP"],
        "PROD": ["TARGET":"PROD","CONFIG":"05-PROD"]
    ]
    public static final String GIT_SISNET_LOCAL_CONFIG_URL = "ssh://git@bitbucket-lab.pelayo.com:7999/pric/pricing-rate-sisnetv6-config.git"

    public static final String APACHE_URI = "/netijam/apache-tomcat-7.0.42";
    public static final String SCRIPTS_URI = "/home/operador"
    public static final String STOP_TOMCAT_URI = SCRIPTS_URI + "/stop-v6.sh || true"
    public static final String WEB_APPS_URI = APACHE_URI + "/webapps/sisnet"
    public static final String WAR_URI = APACHE_URI + "/webapps/sisnet.war"
    public static final String WORK_URI = APACHE_URI + "/work/Catalina/localhost/sisnet"
    public static final String NEW_WAR_PATH = "WebSISnet/target"
    public static final String NEW_WAR_FILE = "sisnet.war"
    public static final String NEW_WAR_GENERATED = "${NEW_WAR_PATH}/${NEW_WAR_FILE}"
    public static final String DEST_WAR = APACHE_URI + "/webapps/sisnet.war"
    public static final String START_TOMCAT_URI = SCRIPTS_URI + "/start-v6.sh || true"

    public static final String OPERADOR_USER = "operador"
    public static final String SERVER_ACPT_1 = "@netijamacpt1"
    public static final String SERVER_ACPT_2 = "@netijamacpt2"
    public static final String SERVER_PREP_1 = "@netijampre1"
    public static final String SERVER_PREP_2 = "@netijampre2"
    public static final String SERVER_ACPT_1_CONNECTION_STRING = OPERADOR_USER + SERVER_ACPT_1
    public static final String SERVER_ACPT_2_CONNECTION_STRING = OPERADOR_USER + SERVER_ACPT_2
    public static final String SERVER_PREP_1_CONNECTION_STRING = OPERADOR_USER + SERVER_PREP_1
    public static final String SERVER_PREP_2_CONNECTION_STRING = OPERADOR_USER + SERVER_PREP_2

	public static final Map artifactRepository = [
        "INTG":["URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/sisnetv6-snapshots/"
            ],
        "ACPT":["URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/sisnetv6-snapshots/"
            ],
        "PREP":["URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/sisnetv6-snapshots/"
            ],
        "PROD":["URL":"http://192.6.3.179:8081/",
                "REPOSITORY":"repository/sisnetv6-releases/"
            ]
        ]

	public static final Map deployCommands = [
        "INTG":[
            "SERVERS":[
                ["USER":"","SERVER":"localhost","IP":"localhost","ONLINE":true,"BATCH":false]
            ],
            "COMMANDS":[
                "${STOP_TOMCAT_URI}",
                "rm -rf ${WEB_APPS_URI}",
                "rm -rf ${WAR_URI}",
                "rm -rf ${WORK_URI}",
                "cp ${NEW_WAR_GENERATED} ${DEST_WAR}",
                "${START_TOMCAT_URI}"
                ]
            ],
        "ACPT":[
            "SERVERS":[
                ["USER":"${OPERADOR_USER}","SERVER":"netijamacpt1","IP":"netijamacpt1","ONLINE":true,"BATCH":false],
                ["USER":"${OPERADOR_USER}","SERVER":"netijamacpt2","IP":"netijamacpt2","ONLINE":true,"BATCH":false]
            ],
            "COMMANDS":[
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## ${STOP_TOMCAT_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WEB_APPS_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WAR_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WORK_URI}",
                "rsync -avz -e 'ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null' --progress ${NEW_WAR_GENERATED} ##USER##@##SERVER##:${DEST_WAR}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## ${START_TOMCAT_URI}"
                ]
        ],
        "PREP":[
           "SERVERS":[
                ["USER":"${OPERADOR_USER}","SERVER":"netijampre1","IP":"netijampre1","ONLINE":true,"BATCH":false],
                ["USER":"${OPERADOR_USER}","SERVER":"netijampre2","IP":"netijampre2","ONLINE":true,"BATCH":false]
            ],
            "COMMANDS":[
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## ${STOP_TOMCAT_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WEB_APPS_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WAR_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WORK_URI}",
                "rsync -avz -e 'ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null' --progress ${NEW_WAR_GENERATED} ##USER##@##SERVER##:${DEST_WAR}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## ${START_TOMCAT_URI}"
                ]
         ],
        "PROD":[
            "SERVERS":[
                ["USER":"${OPERADOR_USER}","SERVER":"xxx","IP":"xxx","ONLINE":true,"BATCH":false]
            ],
            "COMMANDS":[
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## ${STOP_TOMCAT_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WEB_APPS_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WAR_URI}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## rm -rf ${WORK_URI}",
                "rsync -avz -e 'ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null' --progress ${NEW_WAR_GENERATED} ##USER##@##SERVER##:${DEST_WAR}",
                "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ##USER##@##SERVER## ${START_TOMCAT_URI}"
                ]
        ],
    ]
	public static final String gitBBDDRepo="ssh://git@bitbucket-lab.pelayo.com:7999/pric/pricing-rate-sisnetv6-bdd.git"
}
