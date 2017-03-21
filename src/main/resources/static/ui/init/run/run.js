app.run(['$http', '$location', '$state', '$window', 'notifyCode', 'PersonService', '$rootScope', '$log', '$stomp', 'defaultErrorMessageResolver', 'ModalProvider',
    function ($http, $location, $state, $window, notifyCode, PersonService, $rootScope, $log, $stomp, defaultErrorMessageResolver, ModalProvider) {

        defaultErrorMessageResolver.getErrorMessages().then(function (errorMessages) {
            errorMessages['fieldRequired'] = 'هذا الحقل مطلوب';
        });

        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {
            switch (toState.name) {
                case 'home': {
                    $rootScope.pageTitle = 'الرئيسية';
                    $rootScope.pageTitleIcon = 'fa fa-desktop';
                    $rootScope.MDLIcon = 'widgets';
                    $rootScope.helpUrl = '/ui/partials/help/realTimeData.html';
                    break;
                }
                case 'menu': {
                    $rootScope.pageTitle = 'البرامج';
                    $rootScope.pageTitleIcon = 'fa fa-laptop';
                    $rootScope.MDLIcon = 'dashboard';
                    break;
                }
                case 'company': {
                    $rootScope.pageTitle = 'الشركات';
                    $rootScope.pageTitleIcon = 'fa fa-fort-awesome';
                    $rootScope.MDLIcon = 'account_balance';
                    break;
                }
                case 'branch': {
                    $rootScope.pageTitle = 'الفروع';
                    $rootScope.pageTitleIcon = 'fa fa-cubes';
                    $rootScope.MDLIcon = 'layers';
                    break;
                }
                case 'department': {
                    $rootScope.pageTitle = 'الأقسام';
                    $rootScope.pageTitleIcon = 'fa fa-sitemap';
                    $rootScope.MDLIcon = 'store';
                    break;
                }
                case 'employee': {
                    $rootScope.pageTitle = 'الموظفون';
                    $rootScope.pageTitleIcon = 'fa fa-user-circle';
                    $rootScope.MDLIcon = 'people_online';
                    break;
                }
                case 'team': {
                    $rootScope.pageTitle = 'مجموعة الصلاحيات';
                    $rootScope.pageTitleIcon = 'fa fa-shield';
                    $rootScope.MDLIcon = 'settings_input_composite';
                    break;
                }
                case 'person': {
                    $rootScope.pageTitle = 'المستخدمون';
                    $rootScope.pageTitleIcon = 'fa fa-user';
                    $rootScope.MDLIcon = 'lock';
                    break;
                }
                case 'profile': {
                    $rootScope.pageTitle = 'الملف الشخصي';
                    $rootScope.pageTitleIcon = 'fa fa-info-circle';
                    $rootScope.helpUrl = '/ui/partials/help/profile.html';
                    $rootScope.MDLIcon = 'info';
                    break;
                }
                case 'task': {
                    $rootScope.pageTitle = 'إدارة المهام';
                    $rootScope.pageTitleIcon = 'fa fa-tasks';
                    $rootScope.MDLIcon = 'assignment';
                    break;
                }
                case 'reportModel': {
                    $rootScope.pageTitle = 'نماذج الطباعة';
                    $rootScope.pageTitleIcon = 'fa fa-print';
                    $rootScope.MDLIcon = 'description';
                    break;
                }
                case 'help': {
                    $rootScope.pageTitle = 'المساعدة';
                    $rootScope.pageTitleIcon = 'fa fa-info-circle';
                    break;
                }
            }
        });

        $rootScope.contains = function (list, values) {
            return _.intersection(values, list).length > 0;
        };

        $rootScope.logout = function () {
            $http.post('/logout');
            $window.location.href = '/logout';
        };

        $rootScope.ModalProvider = ModalProvider;

        PersonService.findAuthorities().then(function (data) {
            $rootScope.authorities = data;
        });

        /**************************************************************
         *                                                            *
         * STOMP Connect                                              *
         *                                                            *
         *************************************************************/
        $rootScope.chats = [];
        $stomp.connect('/ws').then(function () {
            $stomp.subscribe('/user/queue/notify', function (payload, headers, res) {
                if (payload.code === notifyCode.CHAT) {
                    $rootScope.chats.push(payload);
                    if (!$rootScope.$$phase) {
                        $rootScope.$apply();
                    }
                    $rootScope.showToast(payload.message);
                } else {
                    $rootScope.showToast(payload.message);
                }
            }, {'headers': 'notify'});
        });
        $rootScope.today = new Date();

    }]);