app.controller("closeRequestsCtrl", ['$scope', '$rootScope', '$timeout', 'TaskService', 'TaskOperationService', 'TaskCloseRequestService',
    function ($scope, $rootScope, $timeout, TaskService, TaskOperationService, TaskCloseRequestService) {

        $scope.fetchThisDay = function () {
            $scope.viewType='طلبات الإغلاق يومياً';
            $rootScope.showToast("جاري تحميل طلبات الإغلاق لهذا اليوم، فضلاً انتظر قليلاً");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Day');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showToast("تم تحميل طلبات الإغلاق لهذا اليوم بنجاح");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisWeek = function () {
            $scope.viewType='طلبات الإغلاق اسبوعياً';
            $rootScope.showToast("جاري تحميل طلبات الإغلاق لهذا الأسبوع، فضلاً انتظر قليلاً");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Week');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showToast("تم تحميل طلبات الإغلاق لهذا الأسبوع بنجاح");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisMonth = function () {
            $scope.viewType='طلبات الإغلاق شهرياً';
            $rootScope.showToast("جاري تحميل طلبات الإغلاق لهذا الشهر، فضلاً انتظر قليلاً");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Month');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showToast("تم تحميل طلبات الإغلاق لهذا الشهر بنجاح");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchThisYear = function () {
            $scope.viewType='طلبات الإغلاق سنوياً';
            $rootScope.showToast("جاري تحميل طلبات الإغلاق لهذا العام، فضلاً انتظر قليلاً");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('Year');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showToast("تم تحميل طلبات الإغلاق لهذا العام بنجاح");
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 500);
            })
        };

        $scope.fetchAllTime = function () {
            $scope.viewType='جميع طلبات الإغلاق';
            $rootScope.showToast("جاري تحميل جميع طلبات الإغلاق، فضلاً انتظر قليلاً");
            var search = [];
            search.push('taskPersonId=');
            search.push($scope.me.id);
            search.push('&');
            search.push('timeType=');
            search.push('All');
            search.push('&');
            TaskCloseRequestService.filter(search.join("")).then(function (data) {
                $scope.closeRequests = data;
                $rootScope.showToast("تم تحميل جميع طلبات الإغلاق بنجاح");
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
