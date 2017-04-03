var app = angular.module('Application',
    [
        'ui.router',
        'ngAnimate',
        'toggle-switch',
        'jcs-autoValidate',
        'fm',
        'ngSanitize',
        'counter',
        'FBAngular',
        'ui.bootstrap',
        'angularCSS',
        'smart-table',
        'lrDragNDrop',
        'nya.bootstrap.select',
        'localytics.directives',
        'angularFileUpload',
        'ngLoadingSpinner',
        'ngStomp',
        'luegg.directives',
        'monospaced.elastic',
        'pageslide-directive',
        'ui.bootstrap.contextMenu',
        'kdate',
        'ui.sortable',
        'timer'
    ]);

app.constant('notifyCode', {

    CHAT: 'CHAT',

    CREATE_COMPANY: 'CREATE_COMPANY',
    UPDATE_COMPANY: 'UPDATE_COMPANY',
    DELETE_COMPANY: 'DELETE_COMPANY',

    CREATE_BRANCH: 'CREATE_BRANCH',
    UPDATE_BRANCH: 'UPDATE_BRANCH',
    DELETE_BRANCH: 'DELETE_BRANCH',

    CREATE_DEPARTMENT: 'CREATE_DEPARTMENT',
    UPDATE_DEPARTMENT: 'UPDATE_DEPARTMENT',
    DELETE_DEPARTMENT: 'DELETE_DEPARTMENT',

    CREATE_EMPLOYEE: 'CREATE_EMPLOYEE',
    UPDATE_EMPLOYEE: 'UPDATE_EMPLOYEE',
    DELETE_EMPLOYEE: 'DELETE_EMPLOYEE',

    CREATE_PERSON: 'CREATE_PERSON',
    UPDATE_PERSON: 'UPDATE_PERSON',
    DELETE_PERSON: 'DELETE_PERSON',

    CREATE_TEAM: 'CREATE_TEAM',
    UPDATE_TEAM: 'UPDATE_TEAM',
    DELETE_TEAM: 'DELETE_TEAM',
});

app.factory('errorInterceptor', ['$q', '$rootScope', '$location', '$log',
    function ($q, $rootScope, $location, $log) {
        return {
            request: function (config) {
                return config || $q.when(config);
            },
            requestError: function (request) {
                return $q.reject(request);
            },
            response: function (response) {
                return response || $q.when(response);
            },
            responseError: function (response) {
                if (response && response.status === 404) {
                }
                if (response && response.status >= 500) {
                    $rootScope.showTechnicalNotify("الدعم الفني", response.data.message, "error", "fa-ban");
                }
                return $q.reject(response);
            }
        };
    }]);

app.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', '$cssProvider', '$httpProvider',
    function ($stateProvider, $urlRouterProvider, $locationProvider, $cssProvider, $httpProvider) {

        $urlRouterProvider.otherwise("/menu");

        $locationProvider.html5Mode(true);

        $httpProvider.interceptors.push('errorInterceptor');

        angular.extend($cssProvider.defaults, {
            container: 'head',
            method: 'append',
            persist: true,
            preload: true,
            bustCache: true
        });

        /**************************************************************
         *                                                            *
         * Home State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("home", {
            url: "/home",
            views: {
                '': {
                    templateUrl: "/ui/partials/home/home.html",
                    controller: "homeCtrl",
                },
                'incomingOperations@home': {
                    templateUrl: "/ui/partials/home/incomingOperations.html",
                    controller: "incomingOperationsCtrl"
                },
                'outgoingOperations@home': {
                    templateUrl: "/ui/partials/home/outgoingOperations.html",
                    controller: "outgoingOperationsCtrl"
                },
                'incomingTasks@home': {
                    templateUrl: "/ui/partials/home/incomingTasks.html",
                    controller: "incomingTasksCtrl"
                },
                'outgoingTasks@home': {
                    templateUrl: "/ui/partials/home/outgoingTasks.html",
                    controller: "outgoingTasksCtrl"
                },
                'closeRequests@home': {
                    templateUrl: "/ui/partials/home/closeRequests.html",
                    controller: "closeRequestsCtrl"
                },
                'closedSoonIncoming@home': {
                    templateUrl: "/ui/partials/home/closedSoonIncoming.html",
                    controller: "closedSoonIncomingCtrl"
                },
                'closedSoonOutgoing@home': {
                    templateUrl: "/ui/partials/home/closedSoonOutgoing.html",
                    controller: "closedSoonOutgoingCtrl"
                }
            }
        });

        /**************************************************************
         *                                                            *
         * Menu State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("menu", {
            url: "/menu",
            templateUrl: "/ui/partials/menu/menu.html",
            controller: "menuCtrl"
        });

        /**************************************************************
         *                                                            *
         * Company State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("company", {
            url: "/company",
            templateUrl: "/ui/partials/company/company.html",
            controller: "companyCtrl"
        });

        /**************************************************************
         *                                                            *
         * Region State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("region", {
            url: "/region",
            templateUrl: "/ui/partials/region/region.html",
            controller: "regionCtrl"
        });

        /**************************************************************
         *                                                            *
         * Branch State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("branch", {
            url: "/branch",
            templateUrl: "/ui/partials/branch/branch.html",
            controller: "branchCtrl"
        });

        /**************************************************************
         *                                                            *
         * Department State                                           *
         *                                                            *
         *************************************************************/
        $stateProvider.state("department", {
            url: "/department",
            templateUrl: "/ui/partials/department/department.html",
            controller: "departmentCtrl"
        });

        /**************************************************************
         *                                                            *
         * Employee State                                             *
         *                                                            *
         *************************************************************/
        $stateProvider.state("employee", {
            url: "/employee",
            templateUrl: "/ui/partials/employee/employee.html",
            controller: "employeeCtrl"
        });

        /**************************************************************
         *                                                            *
         * Person State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("person", {
            url: "/person",
            templateUrl: "/ui/partials/person/person.html",
            controller: "personCtrl"
        });

        /**************************************************************
         *                                                            *
         * Team State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("team", {
            url: "/team",
            templateUrl: "/ui/partials/team/team.html",
            controller: "teamCtrl"
        });

        /**************************************************************
         *                                                            *
         * Help State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("help", {
            url: "/help",
            templateUrl: "/ui/partials/help/help.html",
            controller: "helpCtrl"
        });

        /**************************************************************
         *                                                            *
         * Profile State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("profile", {
            url: "/profile",
            templateUrl: "/ui/partials/profile/profile.html",
            controller: "profileCtrl"
        });

        /**************************************************************
         *                                                            *
         * Task State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("task", {
            url: "/task",
            templateUrl: "/ui/partials/task/task.html",
            controller: "taskCtrl"
        });

        /**************************************************************
         *                                                            *
         * ReportModel State                                          *
         *                                                            *
         *************************************************************/
        $stateProvider.state("reportModel", {
            url: "/reportModel",
            templateUrl: "/ui/partials/reportModel/reportModel.html",
            controller: "reportModelCtrl"
        });
    }
]);


