/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.http.check.ws

import scala.concurrent.duration.FiniteDuration

import com.softwaremill.quicklens._

final case class WsFrameCheckSequence[+T <: WsFrameCheck](timeout: FiniteDuration, checks: List[T]) {
  require(checks.nonEmpty, "Can't pass empty check sequence")
}

sealed trait WsFrameCheck {
  def name: String
  def isSilent: Boolean
}

final case class WsBinaryFrameCheck(name: String, matchConditions: List[WsBinaryCheck], checks: List[WsBinaryCheck], isSilent: Boolean) extends WsFrameCheck {

  def matching(newMatchConditions: WsBinaryCheck*): WsBinaryFrameCheck = {
    require(!newMatchConditions.contains(null), "Matching conditions can't contain null elements. Forward reference issue?")
    this.modify(_.matchConditions).using(_ ::: newMatchConditions.toList)
  }

  def check(newChecks: WsBinaryCheck*): WsBinaryFrameCheck = {
    require(!newChecks.contains(null), "Checks can't contain null elements. Forward reference issue?")
    this.modify(_.checks).using(_ ::: newChecks.toList)
  }

  def silent: WsBinaryFrameCheck =
    copy(isSilent = true)
}

final case class WsTextFrameCheck(name: String, matchConditions: List[WsTextCheck], checks: List[WsTextCheck], isSilent: Boolean) extends WsFrameCheck {

  def matching(newMatchConditions: WsTextCheck*): WsTextFrameCheck = {
    require(!newMatchConditions.contains(null), "Matching conditions can't contain null elements. Forward reference issue?")
    this.modify(_.matchConditions).using(_ ::: newMatchConditions.toList)
  }

  def check(newChecks: WsTextCheck*): WsTextFrameCheck = {
    require(!newChecks.contains(null), "Checks can't contain null elements. Forward reference issue?")
    this.modify(_.checks).using(_ ::: newChecks.toList)
  }

  def silent: WsTextFrameCheck =
    copy(isSilent = true)
}
