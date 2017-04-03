app.controller("outgoingOperationsCtrl", ['$scope', '$rootScope', '$timeout', 'TaskOperationService', function ($scope, $rootScope, $timeout, TaskOperationService) {

    $scope.fetchThisDay = function () {
        $scope.viewType = 'حركات المهام الصادرة يومياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الصادرة لهذا اليوم، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findOutgoingOperationsForMe("Day").then(function (data) {
            $scope.outgoingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الصادرة لهذا اليوم بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.fetchThisWeek = function () {
        $scope.viewType = 'حركات المهام الصادرة اسبوعياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الصادرة لهذا الأسبوع، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findOutgoingOperationsForMe("Week").then(function (data) {
            $scope.outgoingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الصادرة لهذا الأسبوع بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.fetchThisMonth = function () {
        $scope.viewType = 'حركات المهام الصادرة شهرياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الصادرة لهذا الشهر، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findOutgoingOperationsForMe("Month").then(function (data) {
            $scope.outgoingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الصادرة لهذا الشهر بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.fetchThisYear = function () {
        $scope.viewType = 'حركات المهام الصادرة سنوياً';
        $rootScope.showNotify("الرئيسية", "جاري تحميل حركات المهام الصادرة لهذا العام، فضلاً انتظر قليلاً", "warning", "fa-dashboard");
        TaskOperationService.findOutgoingOperationsForMe("Year").then(function (data) {
            $scope.outgoingOperations = data;
            $rootScope.showNotify("الرئيسية", "تم تحميل حركات المهام الصادرة لهذا العام بنجاح", "success", "fa-dashboard");
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        })
    };

    $scope.refresh = function () {
        switch ($scope.viewType) {
            case 'حركات المهام الصادرة يومياً':
                $scope.fetchThisDay();
                break;
            case 'حركات المهام الصادرة اسبوعياً':
                $scope.fetchThisWeek();
                break;
            case 'حركات المهام الصادرة شهرياً':
                $scope.fetchThisMonth();
                break;
            case 'حركات المهام الصادرة سنوياً':
                $scope.fetchThisYear();
                break;
        }
    };

    $timeout(function () {
        $scope.fetchThisDay();
    }, 2000);

}]);
