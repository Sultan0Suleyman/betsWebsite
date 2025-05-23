interface ContractDTO {
  contractCreationDate: string; // LocalDate → string (ISO-8601 формат)
  contractDateOfExpiry: string; // LocalDate → string
  contractSalary: number;
}

export interface UserDetailsResponse {
  name: string;
  surname: string;
  email: string;
  numberOfPassport: string;
  passportIssueDate: string; // LocalDate → string
  passportIssuingAuthority: string;
  role: string;
  contract: ContractDTO;
}
