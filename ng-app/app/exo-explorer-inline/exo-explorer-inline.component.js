/**
 * Register the `exoExplorerInline` component on the `exoExplorerInline` module,
 */
angular.module('exoExplorerInline').component('exoExplorerInline', {
  templateUrl : function($routeParams) { // $element, $attrs, $log
  	//$log.info('determining template to be used');
  	var url = 'https://peter.exoplatform.com.ua:8443/portal/_component/documents';
    if($routeParams.driveName) {
    	url = url + '?driveName=' + $routeParams.driveName;
    }
    return url;
  }
});