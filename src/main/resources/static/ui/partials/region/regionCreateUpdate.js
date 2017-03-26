app.controller('regionCreateUpdateCtrl', ['RegionService', 'PersonService', 'CompanyService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'region',
    function (RegionService, PersonService, CompanyService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, region) {

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("المناطق", "تم تحميل بيانات المستخدمين بنجاح", "success", "fa-map-marker");
            });
        };

        $scope.fetchCompaniesData = function () {
            CompanyService.fetchTableData().then(function (data) {
                $scope.companies = data;
                $rootScope.showNotify("المناطق", "تم تحميل بيانات الشركات بنجاح", "success", "fa-map-marker");
            });
        };

        $timeout(function () {
            $rootScope.showNotify("المناطق", "جاري تحميل بيانات الشركات والمستخدمين، فضلاَ انتظر قليلاً", "warning", "fa-map-marker");
            $scope.fetchCompaniesData();
            $scope.fetchPersonData();
        }, 1500);

        if (region) {
            $scope.region = region;
        } else {
            $scope.region = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $rootScope.showNotify("المناطق", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-map-marker");
            switch ($scope.action) {
                case 'create' :
                    RegionService.create($scope.region).then(function (data) {
                        $scope.region = {};
                        $scope.form.$setPristine();
                        $rootScope.showNotify("المناطق", "تم القيام بالعملية بنجاح، يمكنك اضافة فرع آخر الآن", "success", "fa-map-marker");
                    });
                    break;
                case 'update' :
                    RegionService.update($scope.region).then(function (data) {
                        $scope.region = data;
                        $rootScope.showNotify("المناطق", "تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن", "success", "fa-map-marker");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);