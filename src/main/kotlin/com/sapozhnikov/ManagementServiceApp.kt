package com.sapozhnikov

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sapozhnikov.resource.UserResource
import io.dropwizard.Application
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.federecio.dropwizard.swagger.SwaggerBundle
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration
import org.jdbi.v3.core.h2.H2DatabasePlugin

class ManagementServiceApp : Application<ManagementServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args : Array<String>) = ManagementServiceApp().run(*args)
    }

    override fun getName(): String {
        return "user-management-service"
    }

    override fun initialize(bootstrap: Bootstrap<ManagementServiceConfiguration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())

        bootstrap.addBundle(object : SwaggerBundle<ManagementServiceConfiguration>() {
            override fun getSwaggerBundleConfiguration(configuration: ManagementServiceConfiguration): SwaggerBundleConfiguration {
                return SwaggerBundleConfiguration().apply {
                    resourcePackage = "com.sapozhnikov"
                    title = "user management server"
                    description = "Generated documentation for REST API server."
                }
            }
        })

//        bootstrap.addBundle(object : MigrationsBundle<ManagementServiceConfiguration>() {
//            override fun getDataSourceFactory(configuration: ManagementServiceConfiguration): PooledDataSourceFactory {
//                return configuration.database
//            }
//
//
//        })


    }

    override fun run(configuration: ManagementServiceConfiguration, environment: Environment) {
        val factory = JdbiFactory()
        val jdbi = factory.build(environment, configuration.database, "h2")
        jdbi.installPlugin(H2DatabasePlugin())

        val userResource = UserResource(jdbi)
        environment.jersey().register(userResource)
    }
}