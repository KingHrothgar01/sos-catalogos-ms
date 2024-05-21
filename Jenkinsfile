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
    	stage('Code Checkout') {
      		steps {
      		    // Checkout the code from the repository
        		echo "Checkout SCM"
      		}
    	}
    	stage('Unit Testing') {
      		steps {
      		    // Build the Java Maven Project
      		    echo "Unit Tests"
        		sh 'mvn --batch-mode -Dspring.profiles.active=test test'
      		}
    	}
    	stage('Coverage') {
      		steps {
      		    // JaCoCo
      		    echo "Jacoco"
  		    	sh 'mvn --batch-mode -Dspring.profiles.active=test clean org.jacoco:jacoco-maven-plugin:prepare-agent test package'
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
      		    	sh 'mvn -s $USER_MAVEN_SETTINGS_XML clean package -DskipTests dockerfile:push'
      		    }
      		}
    	}
  	}
}
