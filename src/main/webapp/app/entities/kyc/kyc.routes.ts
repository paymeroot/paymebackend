import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { KycComponent } from './list/kyc.component';
import { KycDetailComponent } from './detail/kyc-detail.component';
import { KycUpdateComponent } from './update/kyc-update.component';
import KycResolve from './route/kyc-routing-resolve.service';

const kycRoute: Routes = [
  {
    path: '',
    component: KycComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: KycDetailComponent,
    resolve: {
      kyc: KycResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: KycUpdateComponent,
    resolve: {
      kyc: KycResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: KycUpdateComponent,
    resolve: {
      kyc: KycResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default kycRoute;
