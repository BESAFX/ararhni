app.controller('branchCreateUpdateCtrl', ['BranchService', 'PersonService', 'RegionService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'branch',
    function (BranchService, PersonService, RegionService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, branch) {

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("الفروع", "تم تحميل بيانات المستخدمين بنجاح", "success", "fa-cubes");
            });
        };

        $scope.fetchRegionsData = function () {
            RegionService.fetchTableData().then(function (data) {
                $scope.regions = data;
                $rootScope.showNotify("الفروع", "تم تحميل بيانات المناطق بنجاح", "success", "fa-cubes");
            });
        };

        $timeout(function () {
            $rootScope.showNotify("الفروع", "جاري تحميل بيانات المناطق والمستخدمين، فضلاَ انتظر قليلاً", "warning", "fa-cubes");
            $scope.fetchRegionsData();
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
            $rootScope.showNotify("الفروع", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-cubes");
            switch ($scope.action) {
                case 'create' :
                    BranchService.create($scope.branch).then(function (data) {
                        $scope.branch = {};
                        $scope.form.$setPristine();
                        $rootScope.showNotify("الفروع", "تم القيام بالعملية بنجاح، يمكنك اضافة فرع آخر الآن", "success", "fa-cubes");
                    });
                    break;
                case 'update' :
                    BranchService.update($scope.branch).then(function (data) {
                        $scope.branch = data;
                        $rootScope.showNotify("الفروع", "تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن", "success", "fa-cubes");
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