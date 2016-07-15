/*global hefest: true*/

var hefest = angular.module('hefest', ['ui.router', 'http-auth-interceptor', 'ngCookies', 'ngResource', 'ui.bootstrap', 'hefest.filters', 'hefest.directives', 'hefest.controllers',
		'pascalprecht.translate', 'fcsa-number' ]);

hefest.config(['$httpProvider',  '$translateProvider', 'USER_ROLES', '$stateProvider', '$urlRouterProvider', function($httpProvider, $translateProvider, USER_ROLES, $stateProvider, $urlRouterProvider) {
	
	'use strict';
	
	$urlRouterProvider.otherwise("home"); 
	
	$stateProvider.state('home', {
		url: "/home",
		templateUrl: "partials/home.html",
		controller: "MainController",
		access: {
           authorizedRoles: [USER_ROLES.all]
        }
	}).state('majstori', {
		url: "/majstori",
		templateUrl : "partials/professionals.html",
		controller : 'ProfessionalsController',
		access: {
           authorizedRoles: [USER_ROLES.all]
        }
	}).state('za-majstore', {
		url :"/za-majstore",
		templateUrl : 'partials/proslandingpage.html',
		controller : 'ProfessionalsLandingPageController',
		access: {
	        authorizedRoles: [USER_ROLES.all]
	    }
	}).state('aktivacija', {
		 url: "/aktivacija",
         templateUrl: 'partials/account/activate.html',
         controller: 'ActivationController',
         access: {
             authorizedRoles: [USER_ROLES.all]
         }
     }).state('greska', {
    	 url: "/greska",
         templateUrl: 'partials/error.html',
         access: {
             authorizedRoles: [USER_ROLES.all]
         }
     }).state('resetiraj-lozinku', {
    	 url: "/resetiraj-lozinku",
         templateUrl: 'partials/account/resetPassword.html',
         controller: 'ResetPasswordController',
         access: {
             authorizedRoles: [USER_ROLES.all]
         }
     }). state('odjava', {
    	 url: "/odjava",
         templateUrl: 'partials/home.html',
         controller: 'LogoutController',
         access: {
             authorizedRoles: [USER_ROLES.all]
         }
     }).state("profil", {
    	url: "/profil",
		templateUrl : 'partials/updateProfessionalProfile.html',
		controller : 'UpdateProfessionalProfileController',
		access: {
			authorizedRoles: [USER_ROLES.professional]
		}
	}).state("profil.lozinka", {
		url: "/lozinka",
		templateUrl : 'partials/account/changePassword.html',
		controller : 'ChangePasswordController',
		access: {
			authorizedRoles: [USER_ROLES.professional]
		}
	}).state("profil.slike", {
		url: "/slike",
		templateUrl : 'partials/professional/pictures.html',
		access: {
			authorizedRoles: [USER_ROLES.professional]
		}
	}).state("profil.podaci", {
		url: "/podaci",
		templateUrl : 'partials/professional/data.html',
		access: {
			authorizedRoles: [USER_ROLES.professional]
		}
	}).state("profil.projekti", {
		url: "/projekti",
		templateUrl : 'partials/professional/projects.html',
		access: {
			authorizedRoles: [USER_ROLES.professional]
		}
	}).state("korisnik", {
    	url: "/moj-profil",
		templateUrl : 'partials/user/updateUserProfile.html',
		controller : 'UpdateUserProfileController',
		access: {
			authorizedRoles: [USER_ROLES.user]
		}
	}).state("korisnik.slike", {
    	url: "/slike",
		templateUrl : 'partials/user/pictures.html',
		access: {
			authorizedRoles: [USER_ROLES.user]
		}
	}).state("korisnik.podaci", {
    	url: "/podaci",
		templateUrl : 'partials/user/data.html',
		access: {
			authorizedRoles: [USER_ROLES.user]
		}
	}).state("korisnik.poslovi", {
    	url: "/poslovi",
		templateUrl : 'partials/user/servicerequests.html',
		access: {
			authorizedRoles: [USER_ROLES.user]
		}
	}).state("korisnik.posao", {
    	url: "/novi-posao",
		templateUrl : 'partials/user/newRequest.html',
		access: {
			authorizedRoles: [USER_ROLES.user]
		}
	}).state("professional", {
    	url: "/professional/:professionalId/:url",
		templateUrl : 'partials/professional/profile.html',
		controller: 'ProfessionalsProfileController',
		access: {
			authorizedRoles: [USER_ROLES.all]
		}
	}).state("project", {
    	url: "/project/:projectId/:url",
		templateUrl : 'partials/project/projectPage.html',
		access: {
			authorizedRoles: [USER_ROLES.all]
		}
	}).state('metrics', {
		url: "/metrics",
        templateUrl: 'partials/metrics.html',
        controller: 'MetricsController',
        access: {
            authorizedRoles: [USER_ROLES.admin]
        }
    });
	
	 $translateProvider.useStaticFilesLoader({
         prefix: 'i18n/',
         suffix: '.json'
     });
	 
	 $translateProvider.preferredLanguage('en');

}]).run(['$rootScope', '$location', '$http', 'AuthenticationSharedService', '$modal', 'Session', 'USER_ROLES',
            function($rootScope, $location, $http, AuthenticationSharedService, $modal, Session, USER_ROLES) {
	'use strict';
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {  		 
	     $rootScope.userRoles = USER_ROLES;
	     AuthenticationSharedService.valid(toState.access.authorizedRoles);	
    });
    // Call when the client is confirmed
    $rootScope.$on('event:auth-loginConfirmed', function(data) {
        $rootScope.authenticated = true;
        if ($location.path() === "/" || $location.path() === "/odjava" || $location.path() === "/home") {
        	if (Session.role === USER_ROLES.professional) {
        		$location.path('/profil/slike');
        	} else {
        		$location.path('/moj-profil/slike');
        	}           
        }
    });

    // Call when the 401 response is returned by the server
    $rootScope.$on('event:auth-loginRequired', function(rejection) {
        Session.invalidate();
        $rootScope.authenticated = false;
        if ($location.path() === "/profil/podaci" || $location.path() === "/profil/slike" || $location.path() === "/profil/projekti") {
        	  $modal.open({
      			templateUrl : 'partials/account/signInModal.html',			
      			controller : 'SignInController'
      		});
        }
    });

    // Call when the 403 response is returned by the server
    $rootScope.$on('event:auth-notAuthorized', function(rejection) {
        $rootScope.errorMessage = 'errors.403';
        $location.path('/greska').replace();
    });

    // Call when the user logs out
    $rootScope.$on('event:auth-loginCancelled', function() {
    	
    });
}]);

angular.module('hefest.controllers', []);
