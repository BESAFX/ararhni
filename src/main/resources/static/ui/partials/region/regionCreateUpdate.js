app.controller('regionCreateUpdateCtrl', ['RegionService', 'PersonService', 'CompanyService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'region',
    function (RegionService, PersonService, CompanyService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, region) {

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showToast("تم تحميل بيانات المستخدمين بنجاح");
            });
        };

        $scope.fetchCompaniesData = function () {
            CompanyService.fetchTableData().then(function (data) {
                $scope.companies = data;
                $rootScope.showToast("تم تحميل بيانات الشركات بنجاح");
            });
        };

        $timeout(function () {
            $rootScope.showToast("جاري تحميل بيانات الشركات والمستخدمين، فضلاَ انتظر قليلاً");
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
            $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
            switch ($scope.action) {
                case 'create' :
                    RegionService.create($scope.region).then(function (data) {
                        $scope.region = {};
                        $scope.form.$setPristine();
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك اضافة فرع آخر الآن");
                    });
                    break;
                case 'update' :
                    RegionService.update($scope.region).then(function (data) {
                        $scope.region = data;
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);