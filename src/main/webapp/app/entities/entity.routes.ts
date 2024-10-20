import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'country',
    data: { pageTitle: 'Countries' },
    loadChildren: () => import('./country/country.routes'),
  },
  {
    path: 'profile',
    data: { pageTitle: 'Profiles' },
    loadChildren: () => import('./profile/profile.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'Customers' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'kyc',
    data: { pageTitle: 'Kycs' },
    loadChildren: () => import('./kyc/kyc.routes'),
  },
  {
    path: 'operator',
    data: { pageTitle: 'Operators' },
    loadChildren: () => import('./operator/operator.routes'),
  },
  {
    path: 'transaction',
    data: { pageTitle: 'Transactions' },
    loadChildren: () => import('./transaction/transaction.routes'),
  },
  {
    path: 'refund',
    data: { pageTitle: 'Refunds' },
    loadChildren: () => import('./refund/refund.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
