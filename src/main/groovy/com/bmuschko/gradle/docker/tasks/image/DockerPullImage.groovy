/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.docker.tasks.image

import com.bmuschko.gradle.docker.DockerRegistryCredentials
import com.bmuschko.gradle.docker.response.PullImageResponseHandler
import com.bmuschko.gradle.docker.response.ResponseHandler
import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import com.bmuschko.gradle.docker.tasks.RegistryCredentialsAware
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

class DockerPullImage extends AbstractDockerRemoteApiTask implements RegistryCredentialsAware {
    private ResponseHandler<Void> responseHandler = new PullImageResponseHandler()

    /**
     * The image repository.
     */
    @Input
    String repository

    /**
     * The image's tag.
     */
    @Input
    @Optional
    String tag

    /**
     * The target Docker registry credentials for pushing image.
     */
    @Nested
    @Optional
    DockerRegistryCredentials registryCredentials

    @Override
    void runRemoteCommand(dockerClient) {
        logger.quiet "Pulling repository '${getRepository()}'."
        def pullImageCmd = dockerClient.pullImageCmd(getRepository())

        if(getTag()) {
            pullImageCmd.withTag(getTag())
        }

        if(getRegistryCredentials()) {
            def authConfig = threadContextClassLoader.createAuthConfig(getRegistryCredentials())
            pullImageCmd.withAuthConfig(authConfig)
        }

        InputStream response = pullImageCmd.exec()
        responseHandler.handle(response)
    }

    void setResponseHandler(ResponseHandler<Void> responseHandler) {
        this.responseHandler = responseHandler
    }
}
