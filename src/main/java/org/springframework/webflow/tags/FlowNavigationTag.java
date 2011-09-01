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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * JSP {@link Tag} implementation displaying a navigation tree for the currently running flow. Displays view states only
 * and is capable of detecing sub-flows being executed. Tries to lookup a {@link ViewStateRenderer} implementation from
 * the {@link ApplicationContext} and falls back to a {@link DefaultViewStateRenderer} if none found.
 * 
 * @author Oliver Gierke
 */
public class FlowNavigationTag extends RequestContextAwareTag {

	private static final long serialVersionUID = 5414366326170852292L;
	private static final String DEFAULT_TITLE_KEY_BASE = "flow.navigation.";

	@Autowired
	private MessageSource messageSource;
	private ViewStateRenderer viewStateRender;

	private String renderer;

	private String titleKeyBase = DEFAULT_TITLE_KEY_BASE;
	private int omitIfShorterThan = 0;

	/**
	 * Creates a new {@link FlowNavigationTag}.
	 */
	public FlowNavigationTag() {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	/**
	 * Sets the key base to be used for internationalization of flow titles. Defaults to {@value #DEFAULT_TITLE_KEY_BASE}.
	 * 
	 * @param titleKeyBase the titleKeyBase to set
	 */
	public void setTitleKeyBase(String titleKeyBase) {
		this.titleKeyBase = StringUtils.hasText(titleKeyBase) ? titleKeyBase : DEFAULT_TITLE_KEY_BASE;
	}

	/**
	 * Configures the number of view states the flow has at least to contain to be displayed. This can be used to disable
	 * navigation to be displayed for short flows. Defaults to 0.
	 * 
	 * @param omitIfShorterThan the omitIfShorterThan to set
	 */
	public void setOmitIfShorterThan(String omitIfShorterThan) {
		this.omitIfShorterThan = Integer.parseInt(omitIfShorterThan);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {

		RequestContext context = RequestContextHolder.getRequestContext();
		NavigationInfo navigactionInfo = getNavigationInfo(context);

		if (navigactionInfo != null) {

			if (navigactionInfo.getRoot().getSize() < omitIfShorterThan) {
				return TagSupport.SKIP_PAGE;
			}

			if (viewStateRender == null) {
				viewStateRender = lookupViewStateRenderer();
			}

			TagWriter writer = new TagWriter(pageContext);
			writer.startTag("div");
			writer.writeAttribute("class", "webflowNavigation");
			navigactionInfo.getRoot().doWithViewStates(viewStateRender, writer);
			writer.endTag();
		}

		return TagSupport.SKIP_BODY;
	}

	/**
	 * Returns the {@link ViewStateRenderer} to be used. If a reference to a Spring bean was given through the
	 * {@code renderer} attribute this one will be looked up from the {@link ApplicationContext}. If nothing was
	 * configured explicitly we inspect the {@link ApplicationContext} for a single available {@link ViewStateRenderer}.
	 * In case we don't find one we use a {@link DefaultViewStateRenderer} or throw an exception in case there's multiple
	 * ones availabel in the {@link ApplicationContext} but none selected explicitly.
	 * 
	 * @return
	 */
	private ViewStateRenderer lookupViewStateRenderer() {

		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext
				.getServletContext());

		if (StringUtils.hasText(renderer)) {
			return context.getBean(renderer, ViewStateRenderer.class);
		}

		Collection<ViewStateRenderer> renderes = context.getBeansOfType(ViewStateRenderer.class).values();

		if (renderes.isEmpty()) {
			return new DefaultViewStateRenderer(messageSource);
		}

		if (renderes.size() == 1) {
			return renderes.iterator().next();
		}

		throw new IllegalStateException(
				"Multiple ViewStateRenderer beans found in ApplicationContext! Manually define which one to use using the 'renderer' attribute!");
	}

	private NavigationInfo getNavigationInfo(RequestContext context) {

		if (context == null) {
			return null;
		}

		Map<String, String> config = new HashMap<String, String>();
		config.put(NavigationInfo.TITLE_KEY_BASE, titleKeyBase);

		return new NavigationInfo(context.getFlowExecutionContext().getActiveSession(), Collections.unmodifiableMap(config));

	}
}
