app.controller('branchCreateUpdateCtrl', ['BranchService', 'PersonService', 'CompanyService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'branch',
    function (BranchService, PersonService, CompanyService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, branch) {

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

        if (branch) {
            $scope.branch = branch;
            if(branch.logo){
                FileService.getSharedLink(branch.logo).then(function (data) {
                    $scope.logoLink = data;
                });
            }
        } else {
            $scope.branch = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
            switch ($scope.action) {
                case 'create' :
                    BranchService.create($scope.branch).then(function (data) {
                        $scope.branch = {};
                        $scope.form.$setPristine();
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك اضافة فرع آخر الآن");
                    });
                    break;
                case 'update' :
                    BranchService.update($scope.branch).then(function (data) {
                        $scope.branch = data;
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadFile'
        });

        uploader.filters.push({
            name: 'syncFilter',
            fn: function (item, options) {
                return this.queue.length < 10;
            }
        });

        uploader.filters.push({
            name: 'asyncFilter',
            fn: function (item, options, deferred) {
                setTimeout(deferred.resolve, 1e3);
            }
        });

        uploader.onAfterAddingFile = function (fileItem) {
            uploader.uploadAll();
        };

        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            $scope.branch.logo = response;
            FileService.getSharedLink(response).then(function (data) {
                $scope.logoLink = data;
            });
        };

    }]);