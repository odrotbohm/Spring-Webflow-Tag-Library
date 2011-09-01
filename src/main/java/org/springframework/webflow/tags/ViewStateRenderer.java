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

import org.springframework.web.servlet.tags.form.TagWriter;


/**
 * Callback interface to allow customization of {@link ViewState} rendering.
 *
 * @author Oliver Gierke
 */
public interface ViewStateRenderer {

	/**
	 * Will be invoked before any {@link ViewState} will be rendered.
	 * 
	 * @param info will never be {@literal null}.
	 * @param writer TODO
	 * @throws Exception
	 */
	void doBefore(NavigationInfo info, TagWriter writer) throws Exception;

	/**
	 * Will be invoked for every {@link ViewState}.
	 * 
	 * @param viewState will never be {@literal null}.
	 * @param info will never be {@literal null}.
	 * @param writer TODO
	 * @throws Exception
	 */
	void doWithViewState(ViewState viewState, NavigationInfo info, TagWriter writer) throws Exception;

	/**
	 * Will be invoked after all {@link ViewState}s have been rendered.
	 * 
	 * @param info will never be {@literal null}.
	 * @param writer TODO
	 * @throws Exception
	 */
	void doAfter(NavigationInfo info, TagWriter writer) throws Exception;
}