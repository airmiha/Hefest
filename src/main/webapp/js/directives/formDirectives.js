hefest.directive('showErrors', ['$timeout', function ($timeout) {
	 'use strict';
	  var linkFn;
	  linkFn = function (scope, el, attrs, formCtrl) {
	    var blurred, inputEl, inputName, inputNgEl;
	    blurred = false;
	    inputEl = el[0].querySelector('[name]');
	    inputNgEl = angular.element(inputEl);
	    inputName = inputNgEl.attr('name');
	    if (!inputName) {
	      throw 'show-errors element has no child input elements with a \'name\' attribute';
	    }
	    
	    inputNgEl.bind('focus', function () {
	    	blurred = false;
	    	return el.toggleClass('has-error', false);	            	
	    });
	    
	    inputNgEl.bind('blur', function () {
	      el.find('input').trigger('input').trigger('change').trigger('keydown');
	      blurred = true;	      
	      return $timeout(function () {	    	
	    	  if(formCtrl[inputName]) {
	    		  el.toggleClass('has-error', formCtrl[inputName].$invalid);
	      	  }
	      }, 100);
	    });
	    scope.$watch(function () {
	      return formCtrl[inputName].$invalid;
	    }, function (newVal) {
	      if (!blurred) {
	        return;
	      }
	      return el.toggleClass('has-error', newVal);
	    });
	    scope.$on('show-errors-check-validity', function () {
	      return el.toggleClass('has-error', formCtrl[inputName].$invalid);
	    });
	    return scope.$on('show-errors-reset', function () {
	      return $timeout(function () {
	        el.removeClass('has-error');
	        blurred = false;
	        return blurred;
	      }, 0, false);
	    });
	  };
	  return {
	    restrict: 'A',
	    require: '^form',
	    compile: function (elem, attrs) {
	      if (!elem.hasClass('form-group')) {
	        throw 'show-errors element does not have the \'form-group\' class';
	      }
	      return linkFn;
	    }
	  };
}]);
	
hefest.directive('ngEnter', function () {
	'use strict';
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
            	setTimeout(function() {
            		scope.$apply(function () {            	
            			scope.$eval(attrs.ngEnter);
            		});
                });
                event.preventDefault();
            }
        });
    };
});
	  
hefest.directive('formAutofillFix', function() {
	'use strict';
	 return function(scope, elem, attrs) {
	    // Fixes Chrome bug: https://groups.google.com/forum/#!topic/angular/6NlucSskQjY
	    elem.prop('method', 'POST');
	
	    // Fix autofill issues where Angular doesn't know about autofilled inputs
	    if(attrs.ngSubmit) {
	      setTimeout(function() {
	        elem.unbind('submit').submit(function(e) {
	          e.preventDefault();
	          elem.find('input, textarea, select').trigger('input').trigger('change').trigger('keydown');
		          scope.$apply(attrs.ngSubmit);
		        });
	      }, 0);
	    }
	 };
});
	  
hefest.directive('autoFocus', ['$timeout', function($timeout) {
	'use strict';
	return {
	    restrict: 'AC',
	        link: function(_scope, _element) {
	            $timeout(function(){
	                _element[0].focus();
	            }, 200);
	        }
	    };
}]);

hefest.directive('checkboxMultiSelect', function($compile) {
	'use strict';
	return {
		restrict : 'E',
		replace : true,
		scope : {
			items : '=options',
			model : '='				
		},
		templateUrl : 'partials/directives/multiselect.checkbox.html',
		controller : function($scope) {
			if (!$scope.model) {
				$scope.model = [];
			}
			
			$scope.checkAll = function() {				
				angular.forEach($scope.items, function(item) {
					item.checked = true;
				});
				$scope.setModelValue();				
			};
			
			$scope.uncheckAll = function() {
				angular.forEach($scope.items, function(item) {
					item.checked = false;
				});
				$scope.setModelValue();
			};
			
			$scope.selectItem = function(item) {
				item.checked = !item.checked;
	            $scope.setModelValue();
			};

			$scope.setModelValue = function() {
				var value = [];
				angular.forEach($scope.items, function(item) {
					if (item.checked) {					
						value.push(item.value);
					}
				});
				$scope.model = value;
			};

			$scope.$watch('model', function(newValue, oldValue) {							
				angular.forEach($scope.items, function(item) {	
					item.checked = $scope.model.indexOf(item.value) > -1;					
				});
				$scope.setModelValue();
			}, true);
		}
	};
});

hefest.directive('scrollToBookmark', function() {
	'use strict';
    return {
        link: function(scope, element, attrs) {
          var value = attrs.scrollToBookmark;
          element.click(function() {
            scope.$apply(function() {
              var selector = "[scroll-bookmark='"+ value +"']";
              var element = $(selector);
              if(element.length) {
            	  window.scrollTo(0, element[0].offsetTop - 100);  // Don't want the top to be the exact element, -100 will go to the top for a little bit more
              }
            });
          });
        }
      };
});