pipeline {
	agent {label "master"}
	
	environment {
	    SONAR_CREDENTIALS = credentials('092a3604-21d9-4423-a638-7ea0cf2e1d2d')
	}
  
  	options {
    	buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
    	disableConcurrentBuilds()
    	timestamps()
  	}
  
	stages {
	    stage('Build & Unit Tests') {
            steps {
                echo 'Building application'
                sh 'mvn --batch-mode -Dspring.profiles.active=test -Drevision=${BUILD_NUMBER} org.jacoco:jacoco-maven-plugin:prepare-agent clean verify'
            }
        }
    	stage('Publish Coverage') {
      		steps {
      		    echo "Publishing JaCoCo Report"
  		    	step([$class: 'JacocoPublisher', 
  					execPattern: 'target/*.exec',
  					classPattern: 'target/classes',
  					sourcePattern: 'src/main/java',
  					exclusionPattern: 'src/test*'
				])
      		}
    	}
    	stage('Dependency Check (SCA)') {
            steps {
                echo "OWASP Dependency-Check"
                sh 'mvn --batch-mode org.owasp:dependency-check-maven:check || true'
            }
        }
        stage('SAST - Semgrep') {
            steps {
                echo "Semgrep Security Scan"
                sh '''
                    pip3 install semgrep --quiet || true
                    semgrep --config=auto --config=p/java --config=p/spring --error --quiet || true
                '''
            }
        }
    	stage('SonarQube Analysis') {
      		steps {
      		    echo "Running SonarQube Analysis"
      		    withSonarQubeEnv(installationName: 'localSonar') {
      		        sh 'mvn --batch-mode -Drevision=${BUILD_NUMBER} -Dsonar.login=$SONAR_CREDENTIALS sonar:sonar'
      		    }
      		}
    	}
    	stage('Docker Build & Push') {
      		steps {
      		    echo "Building and Publishing Docker Image"
      		    configFileProvider([configFile(fileId: '53844f09-dfd0-49ad-b86c-8573c2882609', variable: 'USER_MAVEN_SETTINGS_XML')]){
      		    	sh 'mvn -s $USER_MAVEN_SETTINGS_XML --batch-mode -Drevision=${BUILD_NUMBER} dockerfile:build dockerfile:push'
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
