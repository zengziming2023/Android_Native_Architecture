package com.hele.testplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class TestPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        project.task('myTask') << {
//            println 'myTask is running'
//        }

        project.task("myTask") {
            println('myTask is running')
        }

//        def android = project.extensions.findByType(AppExtension)
    }
}