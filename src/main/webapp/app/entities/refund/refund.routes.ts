import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { RefundComponent } from './list/refund.component';
import { RefundDetailComponent } from './detail/refund-detail.component';
import { RefundUpdateComponent } from './update/refund-update.component';
import RefundResolve from './route/refund-routing-resolve.service';

const refundRoute: Routes = [
  {
    path: '',
    component: RefundComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RefundDetailComponent,
    resolve: {
      refund: RefundResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RefundUpdateComponent,
    resolve: {
      refund: RefundResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RefundUpdateComponent,
    resolve: {
      refund: RefundResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default refundRoute;
