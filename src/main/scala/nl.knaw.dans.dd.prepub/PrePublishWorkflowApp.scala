/**
 * Copyright (C) 2020 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.dd.prepub

import nl.knaw.dans.lib.dataverse.DataverseInstance
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.lib.taskqueue.ActiveTaskQueue

import scala.util.Try

class PrePublishWorkflowApp(configuration: Configuration) extends DebugEnhancedLogging {
  private val dataverse = new DataverseInstance(configuration.dataverse)
  private val mapper = new DansDataVaultMetadataBlockMapper(configuration.pidGeneratorBaseUrl, dataverse)
  private val tasks = new ActiveTaskQueue[WorkFlowVariables]()

  def start(): Try[Unit] = {
    tasks.start()
  }

  def stop(): Try[Unit] = {
    tasks.stop()
  }

  def scheduleVaultMetadataTask(workFlowVariables: WorkFlowVariables): Try[Unit] = {
    tasks.add(new SetVaultMetadataTask(workFlowVariables, dataverse, mapper, configuration.awaitWorkflowPausedStateMaxNumberOfRetries, configuration.awaitWorkflowPausedStateMillisecondsBetweenRetries))
  }
}
