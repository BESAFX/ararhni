app.controller("closeRequestsCtrl", ['$scope', '$rootScope', '$timeout', 'TaskService', 'TaskOperationService', 'TaskCloseRequestService',
    function ($scope, $rootScope, $timeout, TaskService, TaskOperationService, TaskCloseRequestService) {

        $scope.fetchThisDay = function () {
            $scope.viewType='طلبات الإغلاق يومياً';
            $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق لهذا اليوم، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Day');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق لهذا اليوم بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisWeek = function () {
            $scope.viewType='طلبات الإغلاق اسبوعياً';
            $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق لهذا الأسبوع، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Week');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق لهذا الأسبوع بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisMonth = function () {
            $scope.viewType='طلبات الإغلاق شهرياً';
            $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق لهذا الشهر، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Month');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق لهذا الشهر بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisYear = function () {
            $scope.viewType='طلبات الإغلاق سنوياً';
            $rootScope.showNotify("الرئيسية", "جاري تحميل طلبات الإغلاق لهذا العام، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Year');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showNotify("الرئيسية", "تم تحميل طلبات الإغلاق لهذا العام بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchAllTime = function () {
            $scope.viewType='جميع طلبات الإغلاق';
            $rootScope.showNotify("الرئيسية", "جاري تحميل جميع طلبات الإغلاق، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showNotify("الرئيسية", "تم تحميل جميع طلبات الإغلاق بنجاح", "success", "fa-dashboard");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.delete = function (closeRequest) {
            TaskCloseRequestService.remove(closeRequest.id);
        };

        $scope.refresh = function () {
            switch ($scope.viewType){
                case 'طلبات الإغلاق يومياً':
                    $scope.fetchThisDay();
                    break;
                case 'طلبات الإغلاق اسبوعياً':
                    $scope.fetchThisWeek();
                    break;
                case 'طلبات الإغلاق شهرياً':
                    $scope.fetchThisMonth();
                    break;
                case 'طلبات الإغلاق سنوياً':
                    $scope.fetchThisYear();
                    break;
                case 'جميع طلبات الإغلاق':
                    $scope.fetchAllTime();
                    break;
            }
        };

        $timeout(function () {
            $scope.fetchThisDay();
        }, 2000);

    }]);
