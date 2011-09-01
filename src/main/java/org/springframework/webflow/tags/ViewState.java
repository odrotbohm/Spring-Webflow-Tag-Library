/*
 * Copyright 2011 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.tags;

import org.springframework.util.Assert;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.execution.FlowSession;

/**
 * Abstraction of a {@link ViewState}.
 * 
 * @author Oliver Gierke
 */
public class ViewState {

	private final StateDefinition definition;
	private final FlowSession session;

	public ViewState(StateDefinition definition, FlowSession session) {

		Assert.notNull(definition);
		Assert.isTrue(definition.isViewState());

		this.definition = definition;
		this.session = session;
	}

	/**
	 * Returns whether the {@link ViewState} is a node in th sense of being an entry into a subflow or being the currently
	 * selected one.
	 * 
	 * @return
	 */
	public boolean isNode() {
		return isCurrentState() || isSubFlowEntry();
	}

	/**
	 * Returns whether the state is the current one.
	 * 
	 * @return
	 */
	public boolean isCurrentState() {
		return definition.equals(session.getState());
	}

	/**
	 * Returns whether the current state of the session the {@link ViewState} is part of is a {@link SubflowState}.
	 * 
	 * @return
	 */
	public boolean isSubFlowEntry() {
		return session.getState() instanceof SubflowState;
	}

	/**
	 * Returns the caption of the {@link ViewState}. Can be configured using {@code <attribute name="caption" … />} in the
	 * flow definition file.
	 * 
	 * @return
	 */
	public String getCaption() {
		return definition.getCaption();
	}

	/**
	 * Returns the id of the {@link ViewState}.
	 * 
	 * @return
	 */
	public String getId() {
		return definition.getId();
	}
}