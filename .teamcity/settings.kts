import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_1.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2018.1"

project {

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    allowExternalStatus = true
    artifactRules = "explain-test.out"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            scriptContent = """
                apk add --no-cache \
                    make \
                    git
                mkdir -p /go/src/github.com/pingcap/tidb
                cp -Rv .  /go/src/github.com/pingcap/tidb
                pwd
                cd  /go/src/github.com/pingcap/tidb
                make dev
            """.trimIndent()
            dockerImage = "golang:1.11"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
        }
        dockerCommand {
            enabled = false
            commandType = build {
                source = path {
                    path = "Dockerfile"
                }
                commandArgs = "--pull"
            }
        }
    }

    triggers {
        vcs {
        }
    }
})
