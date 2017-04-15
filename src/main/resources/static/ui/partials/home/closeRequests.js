app.controller("closeRequestsCtrl", ['$scope', '$rootScope', '$timeout', 'TaskService', 'TaskOperationService', 'TaskCloseRequestService', 'ModalProvider',
    function ($scope, $rootScope, $timeout, TaskService, TaskOperationService, TaskCloseRequestService, ModalProvider) {

        $scope.fetchThisDay = function () {
            $scope.viewType = 'طلبات الإغلاق/التمديد يومياً';
            // $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق/التمديد لهذا اليوم، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Day');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                // $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق/التمديد لهذا اليوم بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisWeek = function () {
            $scope.viewType = 'طلبات الإغلاق/التمديد اسبوعياً';
            // $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق/التمديد لهذا الأسبوع، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Week');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                // $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق/التمديد لهذا الأسبوع بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisMonth = function () {
            $scope.viewType = 'طلبات الإغلاق/التمديد شهرياً';
            // $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق/التمديد لهذا الشهر، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Month');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                // $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق/التمديد لهذا الشهر بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisYear = function () {
            $scope.viewType = 'طلبات الإغلاق/التمديد سنوياً';
            // $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق/التمديد لهذا العام، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Year');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                // $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق/التمديد لهذا العام بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchAllTime = function () {
            $scope.viewType = 'جميع طلبات الإغلاق/التمديد';
            // $rootScope.showNotify("الرئيسية", "جاري تحميل جميع طلبات الإغلاق/التمديد، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                // $rootScope.showNotify("الرئيسية", "تم تحميل جميع طلبات الإغلاق/التمديد بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.delete = function (closeRequest) {
            $rootScope.showConfirmNotify("طلبات الإغلاق/التمديد", "هل تود حذف الطلب فعلاً؟", "error", "fa-power-off", function () {
                TaskCloseRequestService.remove(closeRequest.id);
            });
        };

        $scope.openClosedModel = function (closeRequest) {
            TaskService.findOne(closeRequest.task.id).then(function (data) {
                ModalProvider.openTaskClosedModel(data);
            });
        };

        $scope.openExtensionModel = function (closeRequest) {
            TaskService.findOne(closeRequest.task.id).then(function (data) {
                ModalProvider.openTaskExtensionModel(data);
            });
        };

        $scope.openCreateWarnModel = function (closeRequest) {
            TaskService.findOne(closeRequest.task.id).then(function (data) {
                ModalProvider.openTaskWarnCreateModel(data);
            });
        };

        $scope.openCreateDeductionModel = function (closeRequest) {
            TaskService.findOne(closeRequest.task.id).then(function (data) {
                ModalProvider.openTaskDeductionCreateModel(data);
            });
        };

        $scope.refresh = function () {
            switch ($scope.viewType){
                case 'طلبات الإغلاق/التمديد يومياً':
                    $scope.fetchThisDay();
                    break;
                case 'طلبات الإغلاق/التمديد اسبوعياً':
                    $scope.fetchThisWeek();
                    break;
                case 'طلبات الإغلاق/التمديد شهرياً':
                    $scope.fetchThisMonth();
                    break;
                case 'طلبات الإغلاق/التمديد سنوياً':
                    $scope.fetchThisYear();
                    break;
                case 'جميع طلبات الإغلاق/التمديد':
                    $scope.fetchAllTime();
                    break;
            }
        };

        $timeout(function () {
            $scope.fetchThisDay();
        }, 2000);

    }]);
