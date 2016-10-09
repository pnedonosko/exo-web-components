/**
 * Web Component client.
 */
(function($, uiRightClickPopupMenu, uiListView, uiSimpleView, uiFileView) {
	
	function pageBaseUrl() {
		var theLocation = window.location;

		var theHostName = theLocation.hostname;
		var theQueryString = theLocation.search;

		if (theLocation.port) {
			theHostName += ":" + theLocation.port;
		}

		return theLocation.protocol + "//" + theHostName;
	}
	
	/**
	 * Component core class.
	 */
	function WebComponent() {

		var prefixUrl = pageBaseUrl(location);
		
		// TODO ?
	}

	var component = new WebComponent();

	return component;

})($, uiRightClickPopupMenu, uiListView, uiSimpleView, uiFileView);

