package com.muco.chatservice.global.config

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories
class R2dbcConfig : AbstractR2dbcConfiguration() {

    companion object {
        const val POSTGRESQL_CONNECTION_FACTORY = "postgresqlConnectionFactory"
        const val POSTGRESQL_CONNECTION_FACTORY_INITIALIZER = "'postgresqlConnectionFactoryInitializer"
        const val DB_SCHEMA_PATH = "sql/db-schema.sql"
    }

    @Bean(name = [POSTGRESQL_CONNECTION_FACTORY])
    override fun connectionFactory(): ConnectionFactory {
        val configuration = PostgresqlConnectionConfiguration.builder()
                .host("127.0.0.1")
                .database("muco")
                .username("postgres")
                .password("yh172839")
                .build()

        return PostgresqlConnectionFactory(configuration)
    }

    @Bean(name = [POSTGRESQL_CONNECTION_FACTORY_INITIALIZER])
    fun initializer(@Qualifier(value = POSTGRESQL_CONNECTION_FACTORY) factory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        val populator = ResourceDatabasePopulator(ClassPathResource(DB_SCHEMA_PATH))
        initializer.setConnectionFactory(factory)
        initializer.setDatabasePopulator(populator)
        return initializer
    }
}
