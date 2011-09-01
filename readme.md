# Spring Webflow tag library #

This library contains some (read: right now exactly) one JSP tags useful with webflow.
Right now we have the following ones:

	<webflow:navigation />
	
This tag renders some kind of breadcrumb navigation from all view states when a flow 
is executing. It supports sub-flows and allows sophisticated customization through the
ViewStateRenderer interface. The default implementation creates an ordere list as follows:

	<div class="webflowNavigation">
		<ol>
			<li>Step one</li>
			<li class="node">Step two
				<ol>
					<li class="currentState node>">Sub step one</li>
					<li>Sub step two</li>
				</ol>
			</li>
		</ol>
	</div>