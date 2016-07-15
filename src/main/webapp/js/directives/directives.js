'use strict';

/* Directives */

angular
		.module('hefest.directives', [])
		.directive('appVersion', [ 'version', function(version) {
			return function(scope, elm, attrs) {
				elm.text(version);
			};
		} ]).directive("ngPlaceholder", function($log, $timeout) {
			return {
				restrict : "A",
				link : function(scope, elem, attrs) {
					var txt = attrs.ngPlaceholder;

					console.log(txt);

					elem.focus(function() {
						if (elem.val() === txt) {
							elem.val("");
						}
						scope.$apply();
					});

					elem.blur(function() {
						if (elem.val() === "") {
							elem.val(txt);
						}
						scope.$apply();
					});

					// Initialise placeholder
					$timeout(function() {
						elem.val(txt);
						scope.$apply();
					});
				}
			};
		})
		.directive('checkboxGroup', function($compile) {
			return {
				restrict : 'E',
				scope : {
					items : '=options',
					model : '='
				},
				templateUrl : 'partials/directives/checkboxgroup.html',
				controller : function($scope) {
					$scope.checkAll = function() {
						angular.forEach($scope.items, function(item) {
							item.checked = true;
						});
						$scope.setModelValue();
					};

					if (!$scope.model) {
						$scope.model = '';
					}

					$scope.uncheckAll = function() {
						angular.forEach($scope.items, function(item) {
							item.checked = false;
						});
						$scope.setModelValue();
					};

					$scope.setModelValue = function() {
						var value = {};
						angular.forEach($scope.items, function(item) {
							if (item.checked) {
								value[item.value] = true;
							}
						});
						$scope.model = value;
					};

					$scope.$watch('model', function(newValue, oldValue) {
						// TODO: Implement actual two way binding, right now changing the model from the outside is used
						// only for resetting the checkboxes
						if (newValue === undefined || newValue === '') {
							$scope.uncheckAll();
						}
					}, true);
				}
			};
		}).directive('dropdownSelect', function() {
			return {
				restrict : 'E',
				scope : {
					title: '@',
					options : '=',
					selected: '=',
					onSelect : '&'
				},
				templateUrl : 'partials/directives/dropdownSelect.html'
			};
		});
