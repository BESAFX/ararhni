app.controller("branchCtrl", ['BranchService', 'PersonService', 'ModalProvider', 'FileService', '$scope', '$rootScope', '$state', '$timeout',
    function (BranchService, PersonService, ModalProvider, FileService, $scope, $rootScope, $state, $timeout) {

        $scope.selected = {};

        $scope.fetchTableData = function () {
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $scope.setSelected(data[0]);
            });
        };

        $scope.setSelected = function (object) {
            if (object) {
                angular.forEach($scope.branches, function (branch) {
                    if (object.id == branch.id) {
                        $scope.selected = branch;
                        return branch.isSelected = true;
                    } else {
                        return branch.isSelected = false;
                    }
                });
            }
        };

        $scope.reload = function () {
            $state.reload();
        };

        $scope.openCreateModel = function () {
            ModalProvider.openBranchCreateModel();
        };

        $scope.openUpdateModel = function (branch) {
            if (branch) {
                ModalProvider.openBranchUpdateModel(branch);
                return;
            }
            ModalProvider.openBranchUpdateModel($scope.selected);
        };

        $scope.removeRow = function (id) {
            var index = -1;
            var branchesArr = eval($scope.branches);
            for (var i = 0; i < branchesArr.length; i++) {
                if (branchesArr[i].id === id) {
                    index = i;
                    break;
                }
            }
            if (index === -1) {
                alert("Something gone wrong");
            }
            $scope.branches.splice(index, 1);
        };

        $scope.delete = function (branch) {
            if (branch) {
                $rootScope.showConfirmNotify("المهام", "هل تود حذف الفرع فعلاً؟", "error", "fa-database", function () {
                    BranchService.remove(branch.id).then(function () {
                        $scope.removeRow(branch.id);
                    });
                });
                return;
            }

            $rootScope.showConfirmNotify("المهام", "هل تود حذف الفرع فعلاً؟", "error", "fa-database", function () {
                BranchService.remove($scope.selected.id).then(function () {
                    $scope.removeRow(branch.id);
                });
            });
        };

        $scope.rowMenu = [
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-plus-square-o fa-lg"></span> اضافة</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openCreateModel();
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-edit fa-lg"></span> تعديل</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.openUpdateModel($itemScope.branch);
                }
            },
            {
                html: '<div style="cursor: pointer;padding: 10px"><span class="fa fa-minus-square-o fa-lg"></span> حذف</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.branch);
                }
            }
        ];

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);