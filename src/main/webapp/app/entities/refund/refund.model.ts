import dayjs from 'dayjs/esm';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IRefund {
  id: number;
  reference?: string | null;
  transactionRef?: string | null;
  refundDate?: dayjs.Dayjs | null;
  refundStatus?: keyof typeof Status | null;
  amount?: number | null;
  transaction?: Pick<ITransaction, 'id'> | null;
  customer?: Pick<ICustomer, 'id'> | null;
}

export type NewRefund = Omit<IRefund, 'id'> & { id: null };
