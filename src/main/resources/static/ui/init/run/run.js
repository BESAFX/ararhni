app.run(['$http', '$location', '$state', '$window', 'notifyCode', 'PersonService', '$rootScope', '$log', '$stomp', 'defaultErrorMessageResolver', 'ModalProvider',
    function ($http, $location, $state, $window, notifyCode, PersonService, $rootScope, $log, $stomp, defaultErrorMessageResolver, ModalProvider) {

        defaultErrorMessageResolver.getErrorMessages().then(function (errorMessages) {
            errorMessages['fieldRequired'] = 'هذا الحقل مطلوب';
        });

        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams, options) {
            $.noty.clearQueue(); // Clears the notification queue
            $.noty.closeAll(); // Close all notifications
            switch (toState.name) {
                case 'home': {
                    $rootScope.pageTitle = 'لوحة التحكم';
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
                case 'region': {
                    $rootScope.pageTitle = 'المناطق';
                    $rootScope.pageTitleIcon = 'fa fa-map-marker';
                    $rootScope.MDLIcon = 'place';
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
                    $rootScope.MDLIcon = 'help';
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

            $rootScope.me = {};

            PersonService.findActivePerson().then(function (data) {
                $rootScope.me = data;
                if (data.employees.length > 0) {
                    $rootScope.me.manager = data.employees[0].department.branch.region.company.manager;
                } else if (data.departments.length > 0) {
                    $rootScope.me.manager = data.departments[0].branch.region.company.manager;
                } else if (data.branches.length > 0) {
                    $rootScope.me.manager = data.branches[0].region.company.manager;
                } else if (data.regions.length > 0) {
                    $rootScope.me.manager = data.regions[0].company.manager;
                } else if (data.companies.length > 0) {
                    $rootScope.me.manager = data.companies[0].manager;
                }
            });

        });

        $rootScope.showNotify = function (title, message, type, icon) {
            noty({
                layout: 'bottomLeft',
                theme: 'metroui', // or relax
                type: type, // success, error, warning, information, notification
                text: '<div class="activity-item text-right"><span>' + title + '</span> <i class="fa ' + icon + '"></i><div class="activity">' + message + '</div></div>',
                dismissQueue: true, // [boolean] If you want to use queue feature set this true
                force: false, // [boolean] adds notification to the beginning of queue when set to true
                maxVisible: 3, // [integer] you can set max visible notification count for dismissQueue true option,
                template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
                timeout: 5000, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
                progressBar: true, // [boolean] - displays a progress bar
                animation: {
                    open: 'animated bounce',
                    close: 'animated bounceOutLeft',
                    easing: 'swing',
                    speed: 500 // opening & closing animation speed
                },
                closeWith: ['hover'], // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
                modal: false, // [boolean] if true adds an overlay
                killer: false, // [boolean] if true closes all notifications and shows itself
                callback: {
                    onShow: function () {
                    },
                    afterShow: function () {
                    },
                    onClose: function () {
                    },
                    afterClose: function () {
                    },
                    onCloseClick: function () {
                    },
                },
                buttons: false // [boolean|array] an array of buttons, for creating confirmation dialogs.
            });
        };

        $rootScope.showConfirmNotify = function (title, message, type, icon, callback) {
            noty({
                layout: 'center',
                theme: 'metroui', // or relax
                type: type, // success, error, warning, information, notification
                text: '<div class="activity-item text-right"><span>' + title + '</span> <i class="fa ' + icon + '"></i><div class="activity">' + message + '</div></div>',
                dismissQueue: true, // [boolean] If you want to use queue feature set this true
                force: false, // [boolean] adds notification to the beginning of queue when set to true
                maxVisible: 3, // [integer] you can set max visible notification count for dismissQueue true option,
                template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
                timeout: false, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
                progressBar: true, // [boolean] - displays a progress bar
                animation: {
                    open: 'animated zoomIn',
                    close: 'animated zoomOut',
                    easing: 'swing',
                    speed: 500 // opening & closing animation speed
                },
                closeWith: ['button'], // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
                modal: false, // [boolean] if true adds an overlay
                killer: false, // [boolean] if true closes all notifications and shows itself
                callback: {
                    onShow: function () {
                    },
                    afterShow: function () {
                    },
                    onClose: function () {
                    },
                    afterClose: function () {
                    },
                    onCloseClick: function () {
                    },
                },
                buttons: [
                    {
                        addClass: 'btn btn-primary', text: 'نعم', onClick: function ($noty) {
                        $noty.close();
                        callback();
                    }
                    },
                    {
                        addClass: 'btn btn-danger', text: 'إلغاء', onClick: function ($noty) {
                        $noty.close();
                    }
                    }
                ]
            });
        };

        $rootScope.showTechnicalNotify = function (title, message, type, icon) {
            noty({
                layout: 'center',
                theme: 'metroui', // or relax
                type: type, // success, error, warning, information, notification
                text: '<div class="activity-item text-right"><span>' + title + '</span> <i class="fa ' + icon + '"></i><div class="activity">' + message + '</div></div>',
                dismissQueue: true, // [boolean] If you want to use queue feature set this true
                force: false, // [boolean] adds notification to the beginning of queue when set to true
                maxVisible: 3, // [integer] you can set max visible notification count for dismissQueue true option,
                template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
                timeout: false, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
                progressBar: true, // [boolean] - displays a progress bar
                animation: {
                    open: 'animated tada',
                    close: 'animated bounceOutUp',
                    easing: 'swing',
                    speed: 500 // opening & closing animation speed
                },
                closeWith: ['button'], // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
                modal: true, // [boolean] if true adds an overlay
                killer: true, // [boolean] if true closes all notifications and shows itself
                buttons: [
                    {
                        addClass: 'btn btn-primary', text: 'إعادة تحميل الصفحة', onClick: function ($noty) {
                        $noty.close();
                        location.reload(true);
                    }
                    },
                    {
                        addClass: 'btn btn-danger', text: 'إغلاق', onClick: function ($noty) {
                        $noty.close();
                    }
                    }
                ]
            });
        };

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
                    $rootScope.showNotify(payload.title, payload.message, payload.type, payload.icon);
                } else {
                    $rootScope.showNotify(payload.title, payload.message, payload.type, payload.icon);
                }
            }, {'headers': 'notify'});
        });
        $rootScope.today = new Date();

        /**************************************************************
         *                                                            *
         * Navigation Callers                                         *
         *                                                            *
         *************************************************************/
        $rootScope.goToCompany = function () {
            $state.go('company');
        };
        $rootScope.goToRegion = function () {
            $state.go('region');
        };
        $rootScope.goToBranch = function () {
            $state.go('branch');
        };
        $rootScope.goToDepartment = function () {
            $state.go('department');
        };
        $rootScope.goToEmployee = function () {
            $state.go('employee');
        };
        $rootScope.goToTeam = function () {
            $state.go('team');
        };
        $rootScope.goToPerson = function () {
            $state.go('person');
        };
        $rootScope.goToReportModel = function () {
            $state.go('reportModel');
        };
        $rootScope.goToTask = function () {
            $state.go('task');
        };
        $rootScope.goToHome = function () {
            $state.go('home');
        };
        $rootScope.goToHelp = function () {
            $state.go('help');
        };

    }]);