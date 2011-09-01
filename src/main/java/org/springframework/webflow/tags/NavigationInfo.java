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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowSession;

/**
 * Value object to abstract general information of a flow navigation.
 * 
 * @author Oliver Gierke
 */
public class NavigationInfo {

	public static final String TITLE_KEY_BASE = "titleKeyBase";

	private final NavigationInfo parent;
	private final NavigationInfo child;
	private final Flow flow;
	private final FlowSession session;
	private final Map<String, String> config;

	public NavigationInfo(FlowSession flowSession, Map<String, String> config) {
		this(flowSession, config, null);
	}

	private NavigationInfo(FlowSession flowSession, Map<String, String> config, NavigationInfo child) {

		Assert.notNull(flowSession);

		FlowSession parent = flowSession.getParent();
		this.parent = parent == null ? null : new NavigationInfo(parent, config, this);
		this.flow = (Flow) flowSession.getDefinition();
		this.session = flowSession;
		this.child = child;
		this.config = config;
	}

	/**
	 * Returns the number of view states the navigation contains.
	 * 
	 * @return
	 */
	public int getSize() {
		return getViewStates().size();
	}

	private List<StateDefinition> getViewStates() {
		List<StateDefinition> viewStates = new ArrayList<StateDefinition>();
		for (String id : flow.getStateIds()) {
			StateDefinition state = flow.getState(id);
			if (state.isViewState()) {
				viewStates.add(state);
			}
		}
		return viewStates;
	}

	private boolean isRootFlow() {
		return parent == null;
	}

	/**
	 * Returns the {@link NavigationInfo} for the current flow.
	 * 
	 * @return
	 */
	public NavigationInfo getRoot() {
		return isRootFlow() ? this : parent.getRoot();
	}

	/**
	 * Returns the configuration value for the given key.
	 * 
	 * @param key
	 * @return
	 */
	public String getConfigValue(String key) {
		return config.get(key);
	}

	public void doWithChildNavigation(ViewStateRenderer callback, TagWriter writer) throws Exception {

		if (child == null) {
			return;
		}

		child.doWithViewStates(callback, writer);
	}

	/**
	 * Invokes the given {@link ViewStateRenderer} for all {@link ViewState}s contained in this {@link NavigationInfo}.
	 * 
	 * @param renderer must not be {@literal null}.
	 * @param writer
	 * @throws Exception
	 */
	public void doWithViewStates(ViewStateRenderer renderer, TagWriter writer) throws Exception {

		renderer.doBefore(this, writer);

		for (String stateId : flow.getStateIds()) {
			StateDefinition state = flow.getState(stateId);
			if (state.isViewState()) {
				ViewState viewState = new ViewState(state, session);
				renderer.doWithViewState(viewState, this, writer);
			}
		}

		renderer.doAfter(this, writer);
	}
}