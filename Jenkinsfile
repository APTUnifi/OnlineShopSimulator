pipeline {
agent any 
stages {
    
    stage("Compile") {
        steps {
            dir("OnlineShopSimulator") {
                sh "./gradlew compileJava"
             }
         }
    }
    
    stage("Unit test") {
            steps {
                    dir("OnlineShopSimulator") {          
                        sh "xvfb-run -a --server-args='-screen 0 1024x768x24' ./gradlew clean test --scan"
                    }
               }
          }    
    
    stage("Integration test") {
        steps {
            dir("OnlineShopSimulator") {
                sh "sudo docker run -d -p 27017:27017 --rm --name mongo mongo:4.0.5"
                sh "xvfb-run -a --server-args='-screen 0 1024x768x24' ./gradlew integrationTest --scan"
            }
       }
    }
 
    stage("E2E test") {
        steps {
            dir("OnlineShopSimulator") {  
                sh "xvfb-run -a --server-args='-screen 0 1024x768x24' ./gradlew e2eTest --scan"
            }
       }
    }
    
    stage("Coverage reports"){
        steps{
            dir("OnlineShopSimulator"){
                jacoco( 
                  execPattern: 'build/jacoco/**.exec',
                  classPattern: 'build/classes*',
                  sourcePattern: 'src/main/java*',
                  exclusionPattern: 'src/test*')
            }
        }  
    }
    
    stage("Build") {
        steps {
            dir("OnlineShopSimulator") {
               sh "xvfb-run -a --server-args='-screen 0 1024x768x24' ./gradlew build --scan"
            }
        }
    }

    stage("Cleaning docker"){
        steps{
            sh "sudo docker stop mongo"
        }
    }
}
}
