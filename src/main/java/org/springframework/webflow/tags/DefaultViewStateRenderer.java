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

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.TagWriter;

/**
 * Default {@link ViewStateRenderer} to render {@link ViewState}s using a {@link TagWriter}.
 * 
 * @author Oliver Gierke
 */
public class DefaultViewStateRenderer implements ViewStateRenderer {

	private final MessageSourceAccessor accessor;

	/**
	 * Creates a new {@link DefaultViewStateRenderer} using the given {@link TagWriter}.
	 * 
	 * @param writer
	 */
	public DefaultViewStateRenderer(MessageSource messageSource) {
		Assert.notNull(messageSource);
		this.accessor = new MessageSourceAccessor(messageSource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.webflow.tags.ViewStateRenderer#doBefore(org.springframework.webflow.tags.NavigationInfo, org.springframework.web.servlet.tags.form.TagWriter)
	 */
	public void doBefore(NavigationInfo info, TagWriter writer) throws Exception {
		writer.startTag("ol");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.webflow.tags.ViewStateRenderer#doAfter(org.springframework.webflow.tags.NavigationInfo, org.springframework.web.servlet.tags.form.TagWriter)
	 */
	public void doAfter(NavigationInfo info, TagWriter writer) throws Exception {
		writer.endTag();
	}

	public void doWithViewState(ViewState viewState, NavigationInfo info, TagWriter writer) throws Exception {

		writer.startTag("li");

		List<String> classes = new ArrayList<String>();
		if (viewState.isCurrentState()) {
			classes.add("currentState");
		}

		if (viewState.isNode()) {
			classes.add("node");
		}
		writer.writeOptionalAttributeValue("class", StringUtils.collectionToDelimitedString(classes, " "));

		if (StringUtils.hasText(viewState.getCaption())) {
			writer.appendValue(viewState.getCaption());
		} else {
			String code = info.getConfigValue(NavigationInfo.TITLE_KEY_BASE) + viewState.getId();
			writer.appendValue(accessor.getMessage(code));
		}

		if (viewState.isSubFlowEntry()) {
			info.doWithChildNavigation(this, writer);
		}

		writer.endTag();
	}
}
