import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ITransaction {
  id: number;
  reference?: string | null;
  transactionDate?: dayjs.Dayjs | null;
  senderNumber?: string | null;
  senderWallet?: string | null;
  receiverNumber?: string | null;
  receiverWallet?: string | null;
  transactionStatus?: keyof typeof Status | null;
  payInStatus?: keyof typeof Status | null;
  payOutStatus?: keyof typeof Status | null;
  amount?: number | null;
  object?: string | null;
  payInFailureReason?: string | null;
  payOutFailureReason?: string | null;
  senderCountryName?: string | null;
  receiverCountryName?: string | null;
  customer?: Pick<ICustomer, 'id'> | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
