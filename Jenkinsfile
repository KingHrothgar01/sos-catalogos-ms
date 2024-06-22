pipeline {
	agent {label "master"}
	
	environment {
	    SONAR_CREDENTIALS = credentials('092a3604-21d9-4423-a638-7ea0cf2e1d2d')
	}
  
  	options {
    	buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
    	disableConcurrentBuilds()
  	}
  
	stages {
	    stage('Cleanup Workspace') {
      		steps {
      		    // Checkout the code from the repository
      		    echo "Cleanup Workspace"
      		    sh 'mvn --batch-mode -Dspring.profiles.active=test clean'
      		}
    	}
    	stage('Coverage') {
      		steps {
      		    // JaCoCo
      		    echo "Jacoco"
  		    	sh 'mvn --batch-mode -Dspring.profiles.active=test -Drevision=${BUILD_NUMBER} org.jacoco:jacoco-maven-plugin:prepare-agent clean test package'
  		    	step([$class: 'JacocoPublisher', 
  					execPattern: 'target/*.exec',
  					classPattern: 'target/classes',
  					sourcePattern: 'src/main/java',
  					exclusionPattern: 'src/test*'
				])
      		}
    	}
    	stage('Code Analysis') {
      		steps {
      		    // SonarQube
      		    echo "SonarQube"
      		    withSonarQubeEnv(installationName: 'localSonar') {
      		        sh 'mvn sonar:sonar -Dsonar.login=$SONAR_CREDENTIALS'
      		    }

      		}
    	}
    	stage('Build Deploy Code') {
      		steps {
      		    // Build the Java Maven Project
      		    echo "Dockerizing Application"
      		    configFileProvider([configFile(fileId: '53844f09-dfd0-49ad-b86c-8573c2882609', variable: 'USER_MAVEN_SETTINGS_XML')]){
      		    	sh 'mvn -s $USER_MAVEN_SETTINGS_XML -Drevision=${BUILD_NUMBER} -DskipTests clean install'
      		    }
      		}
    	}
    	stage('Trigger Config Change Pipeline') {
    		steps {
    		    script {
		       		echo "triggering updatemanifestjob"
		       		
		       		VERSION = sh (
		       			script: 'mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Drevision=${BUILD_NUMBER} -Dexpression=project.version -q -DforceStdout',
		       			returnStdout: true
		       		).trim()

		   			echo "VERSION: ${VERSION}"
		   			build job: 'sos-catalogos-ms-deploy', parameters: [string(name: 'VERSION', value: "${VERSION}")]
		   		}
	        }
    	}
  	}
}
