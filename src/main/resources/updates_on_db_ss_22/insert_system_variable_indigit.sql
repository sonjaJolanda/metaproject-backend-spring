insert into teamproject_db.systemvariable (var_key, name, required, value)
values  ('FAILED_INDIGIT_TRANSFER_ATTEMPTS', 'number of failed attempts after failedINdigitTransferJob stops the transfer', true, '8'),
        ('FAILED_INDIGIT_TRANSFER_INTERVAL', 'milliseconds of the interval in which failedINdigitTransferJob attempts the transfer', true, '1800000');